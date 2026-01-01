package com.practice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.practice.vo.MenuVO;

@Mapper
public interface MenuMapper {

	List<MenuVO> getMenuList();
	int insertMenu(MenuVO menuVO);

}
