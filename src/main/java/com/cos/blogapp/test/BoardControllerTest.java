package com.cos.blogapp.test;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
public class BoardControllerTest {
	
	private final BoardRepository boardRepository;
	
	@GetMapping("/test/board/{id}")
	public Board detail(@PathVariable int id) {
		// 영속성 컨텍스트 = Board(User-있음, List<Comment>-없음)
		return boardRepository.findById(id).get(); // 메세지컨버터 발동!! - json을 만들기위해서 getter를 다 때려줌
	}
}
