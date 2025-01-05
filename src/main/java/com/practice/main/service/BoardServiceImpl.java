package com.practice.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.practice.main.vo.BoardVO;
import com.practice.mapper.BoardMapper;

@Service
public class BoardServiceImpl {
	
	private final BoardMapper homeMapper;
	
	public BoardServiceImpl(BoardMapper homeMapper) {
        this.homeMapper = homeMapper;
    }
	
	public List<BoardVO> getBoardList() {
		return homeMapper.getBoardList();
	}
}
