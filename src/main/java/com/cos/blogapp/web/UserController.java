package com.cos.blogapp.web;

import javax.servlet.http.HttpSession;	

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.web.dto.JoinDto;
import com.cos.blogapp.web.dto.LoginDto;

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
	public String login(LoginDto loginDto) {
		
		// 1. username, password 받기
		System.out.println(loginDto.getUsername());
		System.out.println(loginDto.getPassword());
		
		// 2. DB 조회
		User userEntity = userRepository.mLogin(loginDto.getUsername(), loginDto.getPassword());
		
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
	public String join(JoinDto joinDto) { // username=love&password=1234&email=love@nate.com으로 데이터가 들어온다

		// User 객체에 데이터를 넣고 User 객체로 받기
		userRepository.save(joinDto.toEntity());
		
		return "redirect:/loginForm"; //리다이렉션(http상태코드: 300)
	}
}
