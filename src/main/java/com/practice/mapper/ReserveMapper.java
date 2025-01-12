package com.practice.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.practice.vo.ReserveMngVO;

@Mapper
public interface ReserveMapper {
	Integer insertReserveMng(ReserveMngVO reserveMngVO);

	int getTotalCount(@Param("reserveMngVO") ReserveMngVO reserveMngVO);

	List<ReserveMngVO> getReserveList(@Param("reserveMngVO") ReserveMngVO reserveMngVO,
            @Param("offset") int offset,
            @Param("pageSize") int pageSize);
}
