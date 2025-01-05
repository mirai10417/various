package com.practice.main.vo;

import lombok.Data;

@Data
public class BoardVO {

	private int boardId;
	private int writerId;
	private String ttl;
	private String cn;
	private int readHits;
	private String secretYn;
	private String useYn;
	private String regDt;
	private String modiDt;
}
