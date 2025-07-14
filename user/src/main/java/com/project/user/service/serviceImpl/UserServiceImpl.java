package com.project.user.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.SerializationUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.common.Result.Result;
import com.project.common.Utils.JsonUtil;
import com.project.user.mapper.UserMapper;
import com.project.user.model.dto.req.LoginReq;
import com.project.user.model.dto.req.RegisterReq;
import com.project.user.model.entity.User;
import com.project.user.model.entity.UserDetailsImpl;
import com.project.user.service.UserService;
import com.project.user.util.JwtUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

import static com.project.user.util.JwtUtils.JWT_TTL;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private com.project.user.service.LoginLogService loginLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static final String BLACKLIST_TOKEN_KEY_PREFIX = "auth:token:blacklist:";

    public static final String USER_DETAIL_KEY = "user:details:";
    @Override
    public User findByUsername(String username) {
        User user = this.getOne(new QueryWrapper<User>().eq("username", username));
        return user;
    }

    @Override
    public Result<String> login(LoginReq loginReq){
        // 1. 查询用户
        User user;
        try {
            user = JsonUtil.readValue(redisTemplate.opsForValue().get(USER_DETAIL_KEY + loginReq.getUsername()), User.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        if(user == null){
            user = this.findByUsername(loginReq.getUsername());
        }
        if (user == null) {
            return Result.fail("用户不存在");
        }
        // 2. 验证账户状态
        if (user.getStatus() == 0) {
            return Result.fail("账号已被禁用");
        }
        // 3. 验证密码 (BCrypt校验)
        if (!passwordEncoder.matches(loginReq.getPassword(), user.getPasswordHash())) {
            // 记录登录失败日志
            loginLogService.record(user.getUserId(),0);
            return Result.fail("密码错误");
        }

        // 4. 构造 UserDetails 对象
        UserDetails userDetails = new UserDetailsImpl(user);

        // 5. 构造 Authentication 对象并保存到 SecurityContext
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        try {
            redisTemplate.opsForValue().set(USER_DETAIL_KEY + userDetails.getUsername(), JsonUtil.writeValueAsString(userDetails), JWT_TTL, TimeUnit.MILLISECONDS);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        // 6. 生成 Token
        String token = JwtUtils.createJWT(user.getUsername());
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
}
