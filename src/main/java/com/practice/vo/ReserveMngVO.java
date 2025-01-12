package com.practice.vo;

import lombok.Data;

@Data
public class ReserveMngVO {
	private int roomId;
	private String roomName;
	private int defaultSeatCount;
	private int defaultOperUnit;
	private int defaultOperStartTime;
	private int defaultOperEndTime;
	private String useYn;
	private String delYn;
	private String createdId;
	private String createdAt;
	private String updatedId;
	private String updatedAt;
}
