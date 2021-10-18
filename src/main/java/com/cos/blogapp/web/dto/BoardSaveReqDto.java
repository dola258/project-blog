package com.cos.blogapp.web.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.user.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BoardSaveReqDto {

	@Size(min = 1, max = 50) // valid 체크를 위해서 적는다
	@NotBlank
	private String title;
	private String content; // content는 null도 가능하고 4GB라서 길이 제한도 필요없다
	
	public Board toEntity(User principal) {
		Board board = new Board();
		board.setTitle(title);
		board.setContent(content);
		board.setUser(principal);
		return board;
	}
	
}
