package com.cos.blogapp.web;


import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.web.dto.BoardSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final이 붙은 필드에 대한 생성자가 만들어진다(DI)
@Controller // 컴포넌트 스캔(스프링) IoC
public class BoardController {

	private final BoardRepository boardRepository;
	private final HttpSession session;
	
	//글 작성 기능
	@PostMapping("/board")
	public String save(@Valid BoardSaveReqDto dto) {
		
		User principal = (User) session.getAttribute("principal");
		
		boardRepository.save(dto.toEntity(principal));
		
		return "redirect:/";
	}
	
	
	// 글 작성 호출
	@GetMapping("/board/saveForm")
	public String saveForm() {
		return "board/saveForm";
	}
	
	
	
	// 보드 목록 호출
	@GetMapping("/board")
	public String home(Model model, int page) {
		
		PageRequest pageRequest = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"));
		
		// Sort.by(Sort.Direction.DESC, "id")
		Page<Board> boardsEntity = boardRepository.findAll(pageRequest);
		model.addAttribute("boardsEntity", boardsEntity);
		
		return "board/list";
	}
}
