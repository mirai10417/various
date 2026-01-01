package com.practice.controller.Auth;

import java.time.LocalDateTime;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.config.JwtProperties;
import com.practice.config.JwtProvider;
import com.practice.dto.LoginRequest;
import com.practice.mapper.JwtMapper;
import com.practice.mapper.UserMapper;
import com.practice.vo.RefreshTokenVO;
import com.practice.vo.UserVO;

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
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        System.out.println("★★★★★★★★★Login★★★★★★★★★★★★★★");
        // 1. 사용자 조회
        UserVO user = userMapper.selectUserByEmail(request.email());
        if(user == null){
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
        vo.setExpiresAt(
            LocalDateTime.now().plusDays(jwtProperties.getRefreshExpirationDays())
        );
        vo.setRevoked(false);

        jwtMapper.insertRefreshToken(vo);

        // 7. 응답
        return ResponseEntity.ok(Map.of(
            "accessToken", access,
            "refreshToken", refresh
        ));
    }
    
    @Transactional
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader("Authorization") String header) {

        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(400).build();
        }

        String refreshToken = header.substring(7);

        // 1. JWT 자체 검증
        jwtProvider.validateToken(refreshToken);

        // 2. DB 검증
        RefreshTokenVO saved = jwtMapper.selectRefreshToken(refreshToken);
        if (saved == null) {

            RefreshTokenVO reused = jwtMapper.selectRevokedToken(refreshToken);
            if (reused != null) {
                jwtMapper.updateRefreshRevoke(reused.getUserId());
                return ResponseEntity.status(401).body("Refresh token reused");
            }

            return ResponseEntity.status(401).build();
        }


        String userId = saved.getUserId();
        // 3. User 재조회
        UserVO user = userMapper.selectUserByUserId(userId);
        if (user == null || !"ACTIVE".equals(user.getStatus())) {
            jwtMapper.updateRefreshRevoke(userId);
            return ResponseEntity.status(401).build();
        }

        // 4. rotation
        jwtMapper.updateRefreshRevoke(userId);

        // 5. 새 토큰 발급(role은 db에서)
        String newAccess = jwtProvider.generateAccessToken(userId, user.getRole());
        String newRefresh = jwtProvider.generateRefreshToken(userId);

        RefreshTokenVO newVo = new RefreshTokenVO();
        newVo.setUserId(userId);
        newVo.setToken(newRefresh);
        newVo.setExpiresAt(LocalDateTime.now().plusDays(jwtProperties.getRefreshExpirationDays()));
        newVo.setRevoked(false);

        jwtMapper.insertRefreshToken(newVo);

        return ResponseEntity.ok(Map.of(
            "accessToken", newAccess,
            "refreshToken", newRefresh
        ));
    }
}