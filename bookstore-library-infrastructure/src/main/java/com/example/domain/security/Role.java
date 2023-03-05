package com.example.domain.security;

/**
 * 角色常量类
 * 目前系统中只有2种角色：用户，管理员
 * 为了注解{@link javax.annotation.security.RolesAllowed}中使用方便，定义为字符串常量（非枚举）
 **/
public interface Role {
    String USER = "ROLE_USER";
    String ADMIN = "ROLE_ADMIN";
}
