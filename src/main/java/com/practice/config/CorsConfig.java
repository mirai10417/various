package com.practice.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
public class CorsConfig {
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin("http://localhost:3000");
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
        config.addAllowedHeader("*");
        // config.addExposedHeader("Authorization"); 
        // 현재구조에선 addExposedHeader은 필요없다. 언제 필요한가? 1. accessToken을 응답 헤더로 내려줄 때, 2. Gateway / BFF패턴, 3. 파일 다운로드 응답에서 Authorization 재활용
        // 역할: 응답 Authorization 헤더를 JS가 읽게 해주는 옵션

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
    
}
