package com.practice.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.practice.mapper.BoardMapper;
import com.practice.vo.BoardVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardServiceImpl {

	private final BoardMapper homeMapper;

	public List<BoardVO> getBoardList() {
		return homeMapper.getBoardList();
	}
}
