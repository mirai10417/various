package com.practice.main.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.practice.main.service.MenuServiceImpl;
import com.practice.vo.MenuVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api")
public class MenuController {

	private final MenuServiceImpl menuServiceImpl;

	@GetMapping(value = "/getMenuList")
	public ResponseEntity<?> getMenuList() {
	    Map<String, Object> resultMap = new HashMap<>();
	    List<MenuVO> getMenuList = menuServiceImpl.getMenuList();
	    resultMap.put("data", getMenuList);

	    return ResponseEntity.ok(resultMap); // ✅ 올바른 방식
	}

	@PostMapping("/insertMenu")
	public ResponseEntity<?> insertMenu(@RequestBody MenuVO menuVO) {
	    menuServiceImpl.insertMenu(menuVO);
	    return ResponseEntity.ok("등록 완료");
	}
}
