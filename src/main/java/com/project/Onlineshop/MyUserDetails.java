package com.project.Onlineshop;

import com.project.Onlineshop.Entity.Employee;
import com.project.Onlineshop.Entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class MyUserDetails implements UserDetails {

    private final Object principal;
    private final boolean isEmployee;

    public MyUserDetails(User user) {
        this.principal = user;
        this.isEmployee = false;
    }

    public MyUserDetails(Employee employee) {
        this.principal = employee;
        this.isEmployee = true;
    }

    private User getUser() {
        if (isEmployee) {
            return null;
        }
        return (User) principal;
    }

    private Employee getEmployee() {
        if (isEmployee) {
            return (Employee) principal;
        }
        return null;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (isEmployee) {
            return List.of(new SimpleGrantedAuthority(getEmployee().getRole().getName()));
        }
        return List.of(new SimpleGrantedAuthority(getUser().getRole().getName()));
    }

    @Override
    public String getPassword() {
        if (isEmployee) {
            return getEmployee().getPassword();
        }
        return getUser().getPassword();
    }

    @Override
    public String getUsername() {
        if (isEmployee) {
            return getEmployee().getUsername();
        }
        return getUser().getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (isEmployee) {
            return getEmployee().isEnabled();
        }
        return getUser().isEnabled();
    }
}