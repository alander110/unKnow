package com.project.user.util;

import com.project.user.model.entity.User;
import com.project.user.model.entity.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public class SecurityUtils {

    /**
     * 获取当前登录用户的用户名
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && !(authentication instanceof org.springframework.security.authentication.AnonymousAuthenticationToken)) {
            return authentication.getName();
        }
        return null;
    }

    /**
     * 获取当前登录用户的 UserDetails 对象
     */
    public static UserDetailsImpl getCurrentUserDetails() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof UserDetailsImpl) {
            return (UserDetailsImpl) authentication.getPrincipal();
        }
        return null;
    }

    /**
     * 获取当前登录的 User 实体对象
     */
    public static User getCurrentUser() {
        UserDetailsImpl userDetails = getCurrentUserDetails();
        return userDetails != null ? userDetails.getUser() : null;
    }
}
