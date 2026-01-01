package com.practice.vo;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class RefreshTokenVO {
    private Long id;
    private String userId;
    private String token;
    private LocalDateTime expiresAt;
    private boolean revoked;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
