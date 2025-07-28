package com.project.user.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.github.pagehelper.PageInfo;
import com.project.common.Result.Result;
import com.project.common.Utils.JsonUtil;
import com.project.user.mapper.UserMapper;
import com.project.user.model.dto.req.*;
import com.project.user.model.dto.res.UserInfoRes;
import com.project.user.model.entity.User;
import com.project.user.model.entity.UserDetailsImpl;
import com.project.user.service.LoginLogService;
import com.project.user.service.UserService;
import com.project.user.util.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.project.user.util.JwtUtils.JWT_TTL;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    private final LoginLogService loginLogService;

    private final PasswordEncoder passwordEncoder;

    private final StringRedisTemplate redisTemplate;

    private final UserMapper userMapper;

    public static final String BLACKLIST_TOKEN_KEY_PREFIX = "auth:token:blacklist:";

    public static final String USER_DETAIL_KEY = "user:details:";
    @Override
    public User findByUsername(String username) {
        User user = this.getOne(new QueryWrapper<User>().eq("username", username));
        return user;
    }

    @Override
    public Result<String> login(LoginReq loginReq) {
        User user = null;
        String userKey = USER_DETAIL_KEY + loginReq.getUsername();

        // 1. 尝试从 Redis 获取用户信息
        try {
            String userString = redisTemplate.opsForValue().get(userKey);
            if (userString != null) {
                user = JsonUtil.readValue(userString, User.class);
            }
        } catch (JsonProcessingException e) {
            // 可记录日志或抛出自定义异常
            throw new RuntimeException("Redis缓存反序列化失败", e);
        }

        // 2. Redis未命中，查询数据库
        if (user == null) {
            user = this.findByUsername(loginReq.getUsername());
            // 缓存空值防止缓存穿透
            if (user == null) {
                try {
                    redisTemplate.opsForValue().set(userKey, "", 60, TimeUnit.SECONDS); // 缓存空值60秒
                } catch (Exception ignored) {
                    // 可记录日志
                }
                return Result.fail("用户不存在");
            }
            // 缓存真实用户信息
            try {
                redisTemplate.opsForValue().set(userKey, JsonUtil.writeValueAsString(user), JWT_TTL, TimeUnit.MILLISECONDS);
            } catch (JsonProcessingException e) {
                throw new RuntimeException("缓存用户信息序列化失败", e);
            }
        }

        // 3. 校验账户状态
        if (user.getStatus() == 0) {
            return Result.fail("账号已被禁用");
        }

        // 4. 校验密码
        if (!passwordEncoder.matches(loginReq.getPassword(), user.getPasswordHash())) {
            loginLogService.record(user.getUserId(), 0);
            return Result.fail("密码错误");
        }

        // 5. 构造 UserDetails 和 Authentication
        UserDetails userDetails = new UserDetailsImpl(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 6. 生成 Token
        String token;
        try {
            token = JwtUtils.createJWT(user.getUsername());
        } catch (Exception e) {
            throw new RuntimeException("Token生成失败", e);
        }

        // 7. 记录登录成功日志
        loginLogService.record(user.getUserId(), 1);

        return Result.success(token);
    }


    @Override
    public Result<String> register(RegisterReq registerReq) {
        // 1. 校验用户名是否已存在
        if (this.findByUsername(registerReq.getUsername()) != null) {
            return Result.fail("用户名已存在");
        }
        if(!registerReq.getPassword().equals(registerReq.getConfirmPassword())){
            return Result.fail("两次密码不一致");
        }
        User user = new User();
        BeanUtils.copyProperties(registerReq, user);
        // 2. 对密码进行 BCrypt 加密
        String rawPassword = registerReq.getPassword();
        String encryptedPassword = passwordEncoder.encode(rawPassword);
        user.setPasswordHash(encryptedPassword);
        // 3. 保存用户
        boolean success = this.save(user);
        return success ? Result.success("注册成功") : Result.fail("注册失败");
    }

    @Override
    public Result<String> refreshToken(String refreshToken) throws Exception {
        if (JwtUtils.validateRefreshToken(refreshToken)) {
            String username = JwtUtils.extractUsername(refreshToken);
            String newAccessToken = JwtUtils.createJWT(username);
            return Result.success(newAccessToken);
        } else {
            return Result.fail("无效的 Refresh Token");
        }
    }

    @Override
    public Result<String> logout(String token) throws Exception {
        token = token.substring(7);
        try {
            Claims claims = JwtUtils.parseJWT(token);
            long expirationTime = claims.getExpiration().getTime() - System.currentTimeMillis();
            // 将 Token 加入 Redis 黑名单，并设置与 Token 剩余时间一致的过期时间
            redisTemplate.opsForValue().set(BLACKLIST_TOKEN_KEY_PREFIX + token, "logout", expirationTime, TimeUnit.MILLISECONDS);

            return Result.success("退出成功");
        } catch (Exception e) {
            return Result.fail("无效的 Token");
        }
    }

    @Override
    public User getUserInfo(Long userId) {
        return this.getById(userId);
    }

    @Override
    public Boolean updateUser(UserUpdateReq userUpdateReq) {
        User user = new User();
        BeanUtils.copyProperties(userUpdateReq, user);
        return this.updateById(user);
    }

    @Override
    public Boolean changePassword(ChangePasswordReq changePasswordReq) {
        return this.update(new UpdateWrapper<User>()
                .eq("user_id", changePasswordReq.getUserId())
                .set("password_hash", passwordEncoder.encode(changePasswordReq.getNewPassword())));
    }

    @Override
    public Boolean updateUserStatus(Long userId, int status) {
        return this.update(new UpdateWrapper<User>()
                .eq("user_id", userId)
                .set("status", status));
    }

    @Override
    public PageInfo<UserInfoRes> getUserList(SelectUserReq selectUserReq) {
        // 构建查询条件
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();

        if (StringUtils.hasText(selectUserReq.getUsername())) {
            queryWrapper.like("username", selectUserReq.getUsername());
        }

        if (StringUtils.hasText(selectUserReq.getPhone())) {
            queryWrapper.like("phone", selectUserReq.getPhone());
        }

        if (StringUtils.hasText(selectUserReq.getEmail())) {
            queryWrapper.like("email", selectUserReq.getEmail());
        }

        if (selectUserReq.getStatus() != null) {
            queryWrapper.eq("status", selectUserReq.getStatus());
        }

        if (StringUtils.hasText(selectUserReq.getCreateTimeStart())) {
            queryWrapper.ge("create_time", selectUserReq.getCreateTimeStart());
        }

        if (StringUtils.hasText(selectUserReq.getCreateTimeEnd())) {
            queryWrapper.le("create_time", selectUserReq.getCreateTimeEnd());
        }

        // 执行查询
        List<User> users = userMapper.selectList(queryWrapper);

        // 转换为返回结果
        List<UserInfoRes> userInfoResList = users.stream().map(user -> {
            UserInfoRes userInfoRes = new UserInfoRes();
            BeanUtils.copyProperties(user, userInfoRes);
            return userInfoRes;
        }).collect(Collectors.toList());

        return new PageInfo<>(userInfoResList);
    }

}
