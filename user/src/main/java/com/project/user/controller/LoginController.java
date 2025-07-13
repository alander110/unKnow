package com.project.user.controller;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.project.common.Result.Result;
import com.project.user.model.dto.req.LoginReq;
import com.project.user.model.dto.req.RegisterReq;
import com.project.user.model.entity.User;
import com.project.user.service.LoginLogService;
import com.project.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 登录
 */
@RestController
@Slf4j
@RequestMapping("/api/user/")
@Tag(name = "登录管理", description = "用户注册、登录管理")
public class LoginController {

    @Autowired
    private UserService userService;

    @Autowired
    private LoginLogService loginLogService;

    @Operation(summary = "用户登录")
    @PostMapping("login")
    public Result<String> Login(@RequestBody LoginReq loginReq) {
        // 1. 查询用户
        User user = userService.findByUsername(loginReq.getUsername());
        if (user == null) {
            log.error("用户不存在");
            return Result.fail("用户不存在");
        }
        // 2. 验证账户状态
        if (user.getStatus() == 0) {
            log.error("用户被禁用");
            return Result.fail("账号已被禁用");
        }
        // 3. 验证密码 (BCrypt校验)
        if (!BCrypt.checkpw(loginReq.getPassword(), user.getPasswordHash())) {
            // 记录登录失败日志
            log.error("密码错误");
            loginLogService.record(StpUtil.getLoginIdAsLong(),0);
            return Result.fail("密码错误");
        }
        // 4. 登录操作
        StpUtil.login(user.getUserId());
        // 5. 返回登录信息
        String token = StpUtil.getTokenValue();
        // 6. 记录登录成功日志
        loginLogService.record(StpUtil.getLoginIdAsLong(),1);

        return Result.success(token);
    }

    @Operation(summary = "用户注册")
    @PostMapping("register")
    public Result<String> register(@RequestBody RegisterReq registerReq) {
        // 1. 校验用户名是否已存在
        if (userService.findByUsername(registerReq.getUsername()) != null) {
            return Result.fail("用户名已存在");
        }
        User user = new User();
        BeanUtils.copyProperties(registerReq, user);
        // 2. 对密码进行 BCrypt 加密
        String rawPassword = registerReq.getPassword();
        String hashedPassword = BCrypt.hashpw(rawPassword);
        user.setPasswordHash(hashedPassword);


        // 3. 保存用户
        boolean success = userService.save(user);
        return success ? Result.success("注册成功") : Result.fail("注册失败");
    }


    @Operation(summary = "查询当前用户是否登录")
    @GetMapping("isLogin")
    public Result<Boolean> isLogin() {
        boolean login = StpUtil.isLogin();
        return Result.success(login);
    }


    @Operation(summary = "用户登出")
    @GetMapping("logout")
    public Result logout() {
        StpUtil.logout();
        return Result.success("登出成功");
    }

    @Operation(summary = "第三方登录回调")
    @GetMapping("oauth/{platform}")
    public Result<String> oauthLogin(@PathVariable String platform, @RequestParam String accessToken){

        return null;
    }

}

