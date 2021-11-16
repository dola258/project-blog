 package com.cos.blogapp.web;

import java.util.HashMap;	
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.exception.MyAsyncNotFoundException;
import com.cos.blogapp.service.UserService;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.CMRespDto;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;
import com.cos.blogapp.web.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {
	
	private final UserService userService;
	private final HttpSession session;
	
	// 회원정보 수정--------------------------------------------------------------------------------
	@PutMapping("/user/{id}")                                                // json 통신에서 꼭 필요!!
	public @ResponseBody CMRespDto<String> update(@PathVariable int id, @Valid @RequestBody UserUpdateDto dto, BindingResult bindingResult) {
		// 유효성
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
		
		// 권한
		if(principal.getId() != id) {
			throw  new MyAsyncNotFoundException("회원정보를 수정할 권한이 없습니다.");
		}
		
		userService.회원수정(principal, dto);
		principal.setEmail(dto.getEmail());
		session.setAttribute("principal", principal); // 세션값 변경
		
		return new CMRespDto<String>(1, "성공", null);
	}
	
	
	// 회원정보페이지--------------------------------------------------------------------------------------------------------------------
	@GetMapping("user/{id}")
	public String userInfo(@PathVariable int id) {
		// 기본은 userRepository.findById(id)로 DB에서 가져와야함
		// 편법은 세션에서 값을 가져올 수도 있다 - 인증과 권한 필요 없음
		// 세션에 있는 값을 쓸거라서 모델에 담아 갈 필요가 없다(로그인을 했다)
		
		
		
		return "user/updateForm";
	}
	
	
	//로그아웃 기능-----------------------------------------------------------------------------------------
	@GetMapping("/logout")
	public String logout() {
	//  세션 무효화 -> jsessonId에 있는 값을 비우는것
	//	session.setAttribute("principal", null);
		session.invalidate(); 
		
		return "redirect:/";  
	}
	
	
	//로그인페이지, 회원가입 페이지로 이동------------------------------------------------------------------------
	@GetMapping("/loginForm")
	public String login() {
		return "user/loginForm";
	}
	
	@GetMapping("/joinForm")
	public String join() {
		return "user/joinForm";
	}
	
	
	//로그인 기능-----------------------------------------------------------------------------------------
	@PostMapping("/login")
	public @ResponseBody String login(@Valid LoginReqDto dto, BindingResult bindingResult)  {
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메세지 : " + error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		
		// 1. username, password 받기
		System.out.println(dto.getUsername());
		System.out.println(dto.getPassword());
		
		User userEntity = userService.로그인(dto);
		
		if(userEntity == null) {
			// null이면 loginForm으로 
			return Script.back("아이디/비밀번호를 잘못 입력하였습니다.");
		} else {
			// null이 아니면 session에 User 오브젝트 저장 후 home으로 이동 
			// 세션 날라가는 조건 1. session.invalidate();
			//					  2. 브라우저를 닫으면 날아감 
			session.setAttribute("principal", userEntity);
			return Script.href("/", "로그인 성공");
		}
	}
	
	
	//회원가입 기능-----------------------------------------------------------------------------------------
	@PostMapping("/join")
	public @ResponseBody String join(@Valid JoinReqDto dto, BindingResult bindingResult) { // username=love&password=1234&email=love@nate.com으로 데이터가 들어온다
		
		// 1. 유효성 검사 실패 - 자바스크립트 응답(경고창 띄우고 뒤로가기)
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메세지 : " + error.getDefaultMessage());
			}
			return Script.back(errorMap.toString());
		}
		
		userService.회원가입(dto);
		
		return Script.href("/loginForm"); //리다이렉션(http상태코드: 300)
	}
}
