package com.practice.controller.auth;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.config.JwtProperties;
import com.practice.config.JwtProvider;
import com.practice.dto.LoginRequest;
import com.practice.mapper.JwtMapper;
import com.practice.mapper.UserMapper;
import com.practice.vo.RefreshTokenVO;
import com.practice.vo.UserVO;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtProvider jwtProvider;

    private final JwtMapper jwtMapper;

    private final UserMapper userMapper;

    private final JwtProperties jwtProperties;

    private final PasswordEncoder passwordEncoder;

    @Transactional
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletResponse response) {
        // 1. 사용자 조회
        UserVO user = userMapper.selectUserByEmail(request.email());
        if (user == null) {
            throw new BadCredentialsException("Invalid user");
        }

        // 2. 상태 체크
        if (!user.getStatus().equals("ACTIVE")) {
            throw new DisabledException("User is not active");
        }

        // 3. 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        // 4. 기존 refresh 토큰 revoke
        String userId = String.valueOf(user.getUserId());
        jwtMapper.updateRefreshRevoke(userId);

        // 5. 토큰 발급
        String access = jwtProvider.generateAccessToken(userId, user.getRole());
        String refresh = jwtProvider.generateRefreshToken(userId);

        // 6. refresh 저장
        RefreshTokenVO vo = new RefreshTokenVO();
        vo.setUserId(userId);
        vo.setToken(refresh);
        vo.setExpiresAt(LocalDateTime.now().plusDays(jwtProperties.getRefreshExpirationDays()));
        vo.setRevoked(false);

        jwtMapper.insertRefreshToken(vo);

        // 7. refresh 토큰 쿠키 저장
        Cookie refreshCookie = new Cookie("refreshToken", refresh);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);
        refreshCookie.setSecure(false); //  로컬에서는 쿠키가 아예 안들어가서 false로 해야함
        refreshCookie.setAttribute("SameSite", "Lax"); // 로컬에서는 기본값이 Lax지만 직관적으로 넣어놨음
        // refreshCookie.setSecure(isProd); // 운영환경에서 필수 > HTTPS, prod=true, local=false
        // refreshCookie.setAttribute("SameSite", "Strict"); // 운영환경에서 필수 > SameSite 설정(CSRF 대비)

        response.addCookie(refreshCookie);

        // 8. 응답
        return ResponseEntity.ok(Map.of(
                "accessToken", access,
                "username", user.getName()
        ));
    }
    
    @Transactional
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(HttpServletRequest request, HttpServletResponse response) {

        // 1️⃣ 쿠키에서 refreshToken 추출
        String refreshToken = null;
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2️⃣ JWT 자체 검증
        jwtProvider.validateToken(refreshToken);

        // 3️⃣ DB 검증
        RefreshTokenVO saved = jwtMapper.selectRefreshToken(refreshToken);
        if (saved == null) {

            RefreshTokenVO reused = jwtMapper.selectRevokedToken(refreshToken);
            if (reused != null) {
                jwtMapper.updateRefreshRevoke(reused.getUserId());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Refresh token reused");
            }

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String userId = saved.getUserId();

        // 4️⃣ 사용자 검증
        UserVO user = userMapper.selectUserByUserId(userId);
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            jwtMapper.updateRefreshRevoke(userId);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 5️⃣ rotation (기존 refresh 폐기)
        jwtMapper.updateRefreshRevoke(userId);

        // 6️⃣ 새 토큰 발급
        String newAccess = jwtProvider.generateAccessToken(userId, user.getRole());
        String newRefresh = jwtProvider.generateRefreshToken(userId);

        RefreshTokenVO newVo = new RefreshTokenVO();
        newVo.setUserId(userId);
        newVo.setToken(newRefresh);
        newVo.setExpiresAt(
            LocalDateTime.now().plusDays(jwtProperties.getRefreshExpirationDays())
        );
        newVo.setRevoked(false);

        jwtMapper.insertRefreshToken(newVo);

        // 7️⃣ refreshToken 쿠키 재설정 (HttpOnly)
        Cookie refreshCookie = new Cookie("refreshToken", newRefresh);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setPath("/");
        refreshCookie.setMaxAge(60 * 60 * 24 * 7);
        refreshCookie.setSecure(false); //  로컬에서는 쿠키가 아예 안들어가서 false로 해야함
        refreshCookie.setAttribute("SameSite", "Lax"); // 로컬에서는 기본값이 Lax지만 직관적으로 넣어놨음
        // refreshCookie.setSecure(isProd); // 운영환경에서 필수 > HTTPS, prod=true, local=false
        // refreshCookie.setAttribute("SameSite", "Strict"); // 운영환경에서 필수 > SameSite 설정(CSRF 대비)

        response.addCookie(refreshCookie);

        // 8️⃣ accessToken만 body로 반환
        return ResponseEntity.ok(Map.of(
            "accessToken", newAccess,
            "username", user.getName()
        ));
    }
    
    // @Transactional
    // @PostMapping("/refresh")
    // public ResponseEntity<?> refresh(@RequestHeader("Authorization") String header) {

    //     if (header == null || !header.startsWith("Bearer ")) {
    //         return ResponseEntity.status(400).build();
    //     }

    //     String refreshToken = header.substring(7);

    //     // 1. JWT 자체 검증
    //     jwtProvider.validateToken(refreshToken);

    //     // 2. DB 검증
    //     RefreshTokenVO saved = jwtMapper.selectRefreshToken(refreshToken);
    //     if (saved == null) {

    //         RefreshTokenVO reused = jwtMapper.selectRevokedToken(refreshToken);
    //         if (reused != null) {
    //             jwtMapper.updateRefreshRevoke(reused.getUserId());
    //             return ResponseEntity.status(401).body("Refresh token reused");
    //         }

    //         return ResponseEntity.status(401).build();
    //     }


    //     String userId = saved.getUserId();
    //     // 3. User 재조회
    //     UserVO user = userMapper.selectUserByUserId(userId);
    //     if (user == null || !"ACTIVE".equals(user.getStatus())) {
    //         jwtMapper.updateRefreshRevoke(userId);
    //         return ResponseEntity.status(401).build();
    //     }

    //     // 4. rotation
    //     jwtMapper.updateRefreshRevoke(userId);

    //     // 5. 새 토큰 발급(role은 db에서)
    //     String newAccess = jwtProvider.generateAccessToken(userId, user.getRole());
    //     String newRefresh = jwtProvider.generateRefreshToken(userId);

    //     RefreshTokenVO newVo = new RefreshTokenVO();
    //     newVo.setUserId(userId);
    //     newVo.setToken(newRefresh);
    //     newVo.setExpiresAt(LocalDateTime.now().plusDays(jwtProperties.getRefreshExpirationDays()));
    //     newVo.setRevoked(false);

    //     jwtMapper.insertRefreshToken(newVo);

    //     return ResponseEntity.ok(Map.of(
    //         "accessToken", newAccess,
    //         "refreshToken", newRefresh
    //     ));
    // }
}