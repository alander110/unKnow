package com.project.user.Controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import com.project.common.Result.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录
 */
@RestController
@RequestMapping("/acc/")
public class LoginController {

    @RequestMapping("doLogin")
    public Result<String> doLogin(@RequestParam("name") String name,@RequestParam("pwd") String pwd) {
        if("zhang".equals(name) && "123456".equals(pwd)) {
            StpUtil.login(10001,new SaLoginParameter().setTimeout(60 * 60 * 24 * 7));
            return Result.success(StpUtil.getTokenValue());
        }
        return Result.fail("登录失败");
    }

    @RequestMapping("isLogin")
    public Result<Boolean> isLogin() {
        return Result.success(StpUtil.isLogin());
    }

    @RequestMapping("tokenInfo")
    public Result<SaTokenInfo> tokenInfo() {
        return Result.success(StpUtil.getTokenInfo());
    }

    @RequestMapping("logout")
    public Result logout() {
        StpUtil.logout();
        return Result.success();
    }

}

