package com.practice.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.practice.vo.RefreshTokenVO;

@Mapper
public interface JwtMapper {
    void insertRefreshToken(RefreshTokenVO vo);

    void updateRefreshRevoke(@Param("userId") String userId);

    RefreshTokenVO selectRefreshToken(@Param("token") String token);

    RefreshTokenVO selectRevokedToken(String token);
}
