package com.practice.reserve.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.practice.mapper.ReserveMapper;
import com.practice.vo.ReserveMngVO;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ReserveServiceImpl {

	private final ReserveMapper reserveMapper;

	public Integer insertReserveMng(ReserveMngVO reserveMngVO) {
		return reserveMapper.insertReserveMng(reserveMngVO);
	}

	private int getTotalCount(ReserveMngVO reserveMngVO) {
	    return reserveMapper.getTotalCount(reserveMngVO);
	}

	public Map<String, Object> getReserveList(ReserveMngVO reserveMngVO, int currentPage, int pageSize){
		// Offset 계산
	    int offset = (currentPage - 1) * pageSize;

	    // 데이터 조회
	    List<ReserveMngVO> getList = reserveMapper.getReserveList(reserveMngVO, offset, pageSize);

	    // 총 레코드 수 계산
	    int totalRecords = getTotalCount(reserveMngVO);

	    // 결과 구성
	    Map<String, Object> response = new HashMap<>();
	    response.put("data", getList); // 페이징된 데이터
	    response.put("totalRecords", totalRecords); // 총 레코드 수
	    response.put("currentPage", currentPage); // 현재 페이지
	    response.put("pageSize", pageSize); // 페이지 크기
	    response.put("totalPages", (int) Math.ceil((double) totalRecords / pageSize)); // 총 페이지 수
	    return response;
	}
}
