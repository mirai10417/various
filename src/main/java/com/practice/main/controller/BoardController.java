package com.practice.main.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.practice.main.service.BoardServiceImpl;
import com.practice.main.vo.BoardVO;

@Controller
public class BoardController {
	
	private final BoardServiceImpl homeServiceImpl;
	
	public BoardController(BoardServiceImpl homeServiceImpl) {
        this.homeServiceImpl = homeServiceImpl;
    }

	@ResponseBody
	@GetMapping(value = "/api/test")
	public List<BoardVO> Home() {
		List<BoardVO> getList = homeServiceImpl.getBoardList();
        return getList;
	}
}
