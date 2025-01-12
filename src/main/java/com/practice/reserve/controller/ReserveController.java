package com.practice.reserve.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.practice.reserve.service.ReserveServiceImpl;
import com.practice.vo.ReserveMngVO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class ReserveController {

	private final ReserveServiceImpl reserveServiceImpl;

	@PostMapping(value = "/api/reserve")
	public Integer insertReserveMng(
	        @RequestBody ReserveMngVO reserveMngVO,
	        HttpServletRequest request) {

	    System.out.println(reserveMngVO);
	    Integer count = reserveServiceImpl.insertReserveMng(reserveMngVO);

	    return count;
	}

	@GetMapping(value = "/api/getReserveList")
	public Map<String, Object> getReserveList(
			ReserveMngVO reserveMngVO,
			@RequestParam("currentPage") int currentPage,
	        @RequestParam("pageSize") int pageSize){
		return reserveServiceImpl.getReserveList(reserveMngVO, currentPage, pageSize);
	}
}
