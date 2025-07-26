package com.project.user.controller;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.project.common.Result.Result;
import com.project.user.model.dto.req.ChangePasswordReq;
import com.project.user.model.dto.req.SelectUserReq;
import com.project.user.model.dto.req.UserUpdateReq;
import com.project.user.model.dto.res.UserInfoRes;
import com.project.user.model.entity.LoginLog;
import com.project.user.model.entity.User;
import com.project.user.service.LoginLogService;
import com.project.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 用户管理
 */
@RestController
@RequestMapping("/user/")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final LoginLogService loginLogService;

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    @GetMapping("info")
    public Result<UserInfoRes> getUserInfo(@RequestParam(name = "userId") Long userId){
        User userInfo =userService.getUserInfo(userId);
        LoginLog loginLog = loginLogService.getById(userId);
        if(userInfo != null){
            UserInfoRes userInfoRes = new UserInfoRes();
            BeanUtils.copyProperties(userInfo, userInfoRes);
            BeanUtils.copyProperties(loginLog, userInfoRes);
            return Result.success(userInfoRes);
        } else {
            return Result.fail("用户不存在");
        }
    }

    /**
     * 修改用户信息
     * @param userUpdateReq 用户信息更新请求参数，包含需要修改的用户字段
     * @return 是否更新成功的结果封装
     */
    @PutMapping("update")
    public Result<String> updateUser(@RequestBody UserUpdateReq userUpdateReq){
        Boolean updateFlag = userService.updateUser(userUpdateReq);
        if(updateFlag){
            return Result.success("更新成功");
        }else{
            return Result.fail("更新失败");
        }
    }

    /**
     * 修改密码
     * @param changePasswordReq 修改密码请求参数，包含用户ID和旧密码和新密码
     * @return 修改结果
     */
    @PutMapping("changePassword")
    public Result<String> changePassword(@RequestBody ChangePasswordReq changePasswordReq){

        // 调用服务层修改密码
        Boolean result = userService.changePassword(changePasswordReq);

        if (result) {
            return Result.success("密码修改成功");
        } else {
            return Result.fail("密码修改失败，旧密码错误");
        }
    }

    /**
     * 修改用户状态
     * @param userId 用户ID
     * @param status 状态
     * @return 修改结果
     */
    @PutMapping("UpdateUserStatus/{userId}/{status}")
    public Result<String> updateUserStatus(@PathVariable("userId") Long userId, @PathVariable("status") int status){
        // 调用服务层禁用用户
        Boolean result = userService.updateUserStatus(userId,status);
        if(status == 0){
            if (result) {
                return Result.success("禁用用户成功");
            } else {
                return Result.fail("禁用用户失败");
            }
        }else{
            if (result) {
                return Result.success("启用用户成功");
            } else {
                return Result.fail("启用用户失败");
            }
        }

    }


    /**
     * 获取用户列表(可选择查询条件)
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param selectUserReq 查询条件
     * @return 用户列表
     */
    @PostMapping("list")
    public Result<PageInfo<UserInfoRes>> getUserList(
            @RequestParam(name = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(name = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestBody SelectUserReq selectUserReq) {

        PageHelper.startPage(pageNum, pageSize);
        PageInfo<UserInfoRes> userList = userService.getUserList(selectUserReq);

        return Result.success(userList);
    }

}

