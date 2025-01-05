package com.practice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.practice.main.vo.BoardVO;

@Mapper
public interface BoardMapper {
	List<BoardVO> getBoardList();
}
