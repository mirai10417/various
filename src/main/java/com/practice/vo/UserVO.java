package com.practice.vo;

import lombok.Data;

@Data
public class UserVO {
    private Long userId;
    private String email;
    private String password;
    private String name;
    private String role;
    private String status;
    private String created_at;
    private String updated_at;
    private String last_login_at;
}
