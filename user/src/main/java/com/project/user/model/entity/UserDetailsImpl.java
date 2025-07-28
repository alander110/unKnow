package com.project.user.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor    // 这三个注解可以帮我们自动生成 get、set、有参、无参构造函数
public class UserDetailsImpl implements UserDetails {

    private User user;	// 通过有参构造函数填充赋值的

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {  // 检查账户是否 没过期。
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {   // 检查账户是否 没有被锁定。
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {  //检查凭据（密码）是否 没过期。
        return true;
    }

    @Override
    public boolean isEnabled() {    // 检查账户是否启用。
        return true;
    }

    public User getUser() {
        return user;
    }
}