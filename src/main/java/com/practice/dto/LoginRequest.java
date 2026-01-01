package com.practice.dto;

public record LoginRequest(
    String email,
    String password
) {
}
