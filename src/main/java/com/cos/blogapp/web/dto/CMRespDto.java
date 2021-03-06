package com.cos.blogapp.web.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CMRespDto<T> {
	private int code; // 1: 성공, -1: 실패
	private T body;
	private String msg;
}
