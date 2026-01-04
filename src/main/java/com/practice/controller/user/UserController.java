package com.practice.controller.user;

import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.mapper.UserMapper;
import com.practice.vo.UserVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    
    private final UserMapper userMapper;

    @GetMapping("me")
    public Map<String, Object> me(Authentication authentication) {
        String userId = authentication.getName(); // JWT sub

        UserVO user = userMapper.selectUserByUserId(userId);

        return Map.of(
            "userId", user.getUserId(),
            "username", user.getName(),
            "email", user.getEmail(),
            "role", user.getRole()
        );
    }
}
