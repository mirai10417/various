package com.practice.main.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.practice.mapper.MenuMapper;
import com.practice.vo.MenuVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class MenuServiceImpl {

	private final MenuMapper menuMapper;

	public List<MenuVO> getMenuList(){
		return menuMapper.getMenuList();
	}

	public int insertMenu(MenuVO menuVO){
		return menuMapper.insertMenu(menuVO);
	}
}
