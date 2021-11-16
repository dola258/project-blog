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
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.comment.CommentRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.exception.MyAsyncNotFoundException;
import com.cos.blogapp.handler.exception.MyNotFoundException;
import com.cos.blogapp.service.BoardService;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.BoardSaveReqDto;
import com.cos.blogapp.web.dto.CMRespDto;
import com.cos.blogapp.web.dto.CommentSaveDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final이 붙은 필드에 대한 생성자가 만들어진다(DI)
@Controller // 컴포넌트 스캔(스프링) IoC
public class BoardController {

	// DI
	private final HttpSession session;
	private final BoardService boardService;
	
	
	// 댓글 작성----------------------------------------------------------------------------
	@PostMapping("/board/{boardId}/comment")
	public String commentSave(@PathVariable int boardId, @Valid CommentSaveDto dto) {
		
		User principal = (User) session.getAttribute("principal");
		
		boardService.댓글등록(boardId, dto, principal);
		
		// 이미 만들어진 상세보기를 리다이렉트
		return "redirect:/board/"+boardId; 
	}
	
	
	// 글 수정 기능 -------------------------------------------------------------------------
	@PutMapping("/board/{id}")
	public @ResponseBody CMRespDto<String> update(@PathVariable int id, 
									@RequestBody @Valid BoardSaveReqDto dto, BindingResult bindingResult) {
		
		// 유효성 검사
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메세지 : " + error.getDefaultMessage());
			}
			throw new MyAsyncNotFoundException(errorMap.toString());
		}
		
		// 인증
		User principal = (User) session.getAttribute("principal");

		if(principal == null) {
			throw  new MyAsyncNotFoundException("인증이 되지 않았습니다");
		}
		
		return new CMRespDto<>(1, null, "업데이트 성공");
	}
	
	
	// 글 수정페이지로 이동-----------------------------------------------------------------
	@GetMapping("/board/{id}/updateForm")
	public String boardUpdateForm(@PathVariable int id, Model model) {	
		
		model.addAttribute("boardEntity", boardService.게시글수정페이지이동(id));

		return "board/updateForm";
	}
	
	
	// 글 삭제하기(API(AJAX) 요청)----------------------------------------------------------
	@DeleteMapping("/board/{id}")
	public @ResponseBody CMRespDto<String> deleteById(@PathVariable int id) {

		User principal = (User) session.getAttribute("principal");
		if(principal == null) {
			throw  new MyAsyncNotFoundException("인증이 되지 않았습니다");
		} 
		
		boardService.게시글삭제(id, principal);
		
		return new CMRespDto<String> (1, "성공", null); // @ResponseBody -> 데이터리턴!! (String = text/plain)
	}
	
	
	//글 상세보기---------------------------------------------------------------------
	// 쿼리스트링, @PathVariable => 디비 where 에 걸리는 친구들!!
	// 1. 컨트롤러 선정, 2. Http Method 선정, 3. 받을 데이터가 있는지!! (body, 쿼리스트링, 패스var)
	// 4. 디비에 접근을 해야하면 Model 접근하기 orElse Model에 접근할 필요가 없다.
	@GetMapping("/board/{id}")
	public String detail(@PathVariable int id, Model model) {
		
		// Board 객체에 존재하는 것 (Board-있음, User-있음, List<Commnet>-없음)
		model.addAttribute("boardEntity", boardService.게시글상세보기(id));
		
		return "board/detail"; // ViewResolver 발동
	}
	
	
	//글 작성 기능---------------------------------------------------------------------
	@PostMapping("/board")
	public @ResponseBody String save(@Valid BoardSaveReqDto dto, BindingResult bindingResult) {
		
		// 공통로직 시작
		User principal = (User) session.getAttribute("principal");
		
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
		// 공통로직 끝
		
		// 핵심 로직
		boardService.게시글작성(principal, dto);
		
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
		
		model.addAttribute("boardsEntity", boardService.게시글목록보기(page));
		
		return "board/list";
	}
}
