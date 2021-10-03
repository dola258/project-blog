package com.cos.blogapp.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {
	
	private final UserRepository userRepository;
	private final HttpSession session;
	
	//--------홈페이지, 로그인페이지, 회원가입 페이지로 이동---------
	@GetMapping({"/", "/home"})
	public String home() {
		return "home";
	}
	
	// /WEB-INF/views/user/login.jsp
	@GetMapping("/loginForm")
	public String login() {
		return "user/loginForm";
	}
	
	@GetMapping("/joinForm")
	public String join() {
		return "user/joinForm";
	}
	
	//--------로그인 기능---------
	@PostMapping("/login")
	public String login(@Valid LoginReqDto dto, BindingResult bindingResult, Model model) {
		
		System.out.println("에러사이즈: " + bindingResult.getFieldErrors().size());
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메세지 : " + error.getDefaultMessage());
			}
			model.addAttribute("errorMap", errorMap);
			return "error/error";
		}
		
		// 1. username, password 받기
		System.out.println(dto.getUsername());
		System.out.println(dto.getPassword());
		
		// 2. DB 조회
		User userEntity = userRepository.mLogin(dto.getUsername(), dto.getPassword());
		
		if(userEntity == null) {
			// null이면 loginForm으로 
			return "redirect:/loginForm";
		} else {
			// null이 아니면 session에 User 오브젝트 저장 후 home으로 이동
			session.setAttribute("principal", userEntity);
			return "redirect:/home";
		}
	}
	
	//--------회원가입 기능---------
	@PostMapping("/join")
	public String join(@Valid JoinReqDto dto, BindingResult bindingResult, Model model) { // username=love&password=1234&email=love@nate.com으로 데이터가 들어온다
		
		System.out.println("에러사이즈: " + bindingResult.getFieldErrors().size());
		
		if(bindingResult.hasErrors()) {
			Map<String, String> errorMap = new HashMap<>();
			for(FieldError error : bindingResult.getFieldErrors()) {
				errorMap.put(error.getField(), error.getDefaultMessage());
				System.out.println("필드 : " + error.getField());
				System.out.println("메세지 : " + error.getDefaultMessage());
			}
			model.addAttribute("errorMap", errorMap);
			return "error/error";
		}
		
		// User 객체에 데이터를 넣고 User 객체로 받기
		userRepository.save(dto.toEntity());
		
		return "redirect:/loginForm"; //리다이렉션(http상태코드: 300)
	}
}
