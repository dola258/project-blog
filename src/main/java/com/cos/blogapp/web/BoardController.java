package com.cos.blogapp.web;


import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.exception.MyNotFoundException;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final이 붙은 필드에 대한 생성자가 만들어진다(DI)
@Controller // 컴포넌트 스캔(스프링) IoC
public class BoardController {

	// DI
	private final BoardRepository boardRepository;
	private final HttpSession session;
	// 글 삭제하기--------------------------------------------------------------------
	@DeleteMapping("/board/{id}")
	public @ResponseBody String deleteById(@PathVariable int id) {
			
		boardRepository.deleteById(id);
		return "ok"; // @ResponseBody -> 데이터리턴!! (String = text/plain)
	}
	
	//글 상세보기---------------------------------------------------------------------
	// 쿼리스트링, pathvariable => 디비 where 에 걸리는 친구들!!
	// 1. 컨트롤러 선정, 2. Http Method 선정, 3. 받을 데이터가 있는지!! (body, 쿼리스트링, 패스var)
	// 4. 디비에 접근을 해야하면 Model 접근하기 orElse Model에 접근할 필요가 없다.
	@GetMapping("/board/{id}")
	public String detail(@PathVariable int id, Model model) {
		

		// 방법 1 - orElse는 값을 찾으면 Board가 리턴, 못찾으면 (괄호안 내용 리턴) - 추천 xxx
		/*
		
		Board boardEntity =  boardRepository.findById(id)
				.orElse(new Board(100, "글없어요", "글없어요", null));
		
		*/
		
		// 방법 2 - orElseThrow
		Board boardEntity =  boardRepository.findById(id)
				.orElseThrow(()-> new MyNotFoundException(id+" 못찾았어요") );
	
		
		model.addAttribute("boardEntity", boardEntity);
		
		return "board/detail";
	}
	
	
	//글 작성 기능---------------------------------------------------------------------
	@PostMapping("/board")
	public @ResponseBody String save(@Valid BoardSaveReqDto dto, BindingResult bindingResult) {
		
		User principal = (User) session.getAttribute("principal");
		
		// AOP개념: 핵심기능은 아니고 공통기능-------------------
		
		// 인증 확인
		if(principal == null) {
			return Script.href("/loginForm", "잘못된 접근입니다");
		// 	return Script.back("잘못된 접근입니다"); 뒤로가기
		}
		
		// 유효성 검사
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메세지 : " + error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		// AOP 끝
		
		boardRepository.save(dto.toEntity(principal));
		
	//	return "redirect:/";
		return Script.href("/", "글쓰기 완료");
	}
	
	
	// 글 작성 호출---------------------------------------------------------------------
	@GetMapping("/board/saveForm")
	public String saveForm() {
		return "board/saveForm";
	}
	
	
	
	// 보드 목록 호출---------------------------------------------------------------------
	@GetMapping("/board")
	public String home(Model model, int page) {
		
		PageRequest pageRequest = PageRequest.of(page, 3, Sort.by(Sort.Direction.DESC, "id"));
		
		// Sort.by(Sort.Direction.DESC, "id")
		Page<Board> boardsEntity = boardRepository.findAll(pageRequest);
		model.addAttribute("boardsEntity", boardsEntity);
		
		return "board/list";
	}
}
