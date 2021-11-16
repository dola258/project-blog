package com.cos.blogapp.web.dto;

import javax.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

//DTO = Data Transfer Object(데이터 전송 오브젝트)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class UserUpdateDto {
	@NotBlank
	private String email;
}
