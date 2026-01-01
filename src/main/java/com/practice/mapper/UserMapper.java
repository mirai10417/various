package com.practice.mapper;

import org.apache.ibatis.annotations.Mapper;

import com.practice.vo.UserVO;

@Mapper
public interface UserMapper {
    UserVO selectUserByEmail(String email);
    UserVO selectUserByUserId(String userId);
}
