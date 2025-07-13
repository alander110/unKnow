package com.project.user.service.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.project.common.Result.Result;
import com.project.user.mapper.UserMapper;
import com.project.user.model.dto.req.LoginReq;
import com.project.user.model.dto.req.RegisterReq;
import com.project.user.model.entity.User;
import com.project.user.model.entity.UserDetailsImpl;
import com.project.user.service.UserService;
import com.project.user.util.JwtUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService{

    @Autowired
    private com.project.user.service.LoginLogService loginLogService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public User findByUsername(String username) {
        User user = this.getOne(new QueryWrapper<User>().eq("username", username));
        return user;
    }

    @Override
    public Result<String> login(LoginReq loginReq) {
        // 1. 查询用户
        User user = this.findByUsername(loginReq.getUsername());
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
}
