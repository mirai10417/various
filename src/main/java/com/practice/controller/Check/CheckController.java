package com.practice.controller.Check;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CheckController {

    @GetMapping("/me")
    public String me(Authentication authentication) {
        return authentication.getName();
    }
}
