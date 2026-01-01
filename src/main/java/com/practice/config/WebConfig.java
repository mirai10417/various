package com.practice.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/* Spring MVC 레벨 CORS 설정
 * 적용 위치: DispatcherServlet 이후
 * 대상: @Controller, @RestController
 * 보통 WebMvcConfigurer 구현해서 씀
 * 문제점: Spring Security를 쓰면 Security Filter에서 먼저 차단된다. -> DispatcherServlet(도달도 못함)
*/
// @Configuration
// public class WebConfig implements WebMvcConfigurer {

//     @Override
//     public void addCorsMappings(CorsRegistry registry) {
//         registry.addMapping("/**")
//                 .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000") // 허용할 출처
//                 .allowedMethods("GET", "POST", "PUT", "DELETE") // 허용할 HTTP 메서드
//                 .allowedHeaders("*") // 허용할 헤더
//                 .allowCredentials(true); // 자격 증명 허용 여부
//     }
// }