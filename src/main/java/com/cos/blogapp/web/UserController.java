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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.util.MyAlgorithm;
import com.cos.blogapp.util.SHA256;
import com.cos.blogapp.util.Script;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Controller
public class UserController {
	
	private final UserRepository userRepository;
	private final HttpSession session;
	
	
	// 회원정보페이지-----------------------------
	@GetMapping("user/{id}")
	public String userInfo(@PathVariable int id) {
		// 기본은 userRepository.findById(id)로 DB에서 가져와야함
		// 편법은 세션에서 값을 가져올 수도 있다 - 인증과 권한 필요 없음
		// 세션에 있는 값을 쓸거라서 모델에 담아 갈 필요가 없다(로그인을 했다)
		
		
		
		return "user/updateForm";
	}
	
	
	//--------로그아웃 기능---------
	@GetMapping("/logout")
	public String logout() {
	//  세션 무효화 -> jsessonId에 있는 값을 비우는것
	//	session.setAttribute("principal", null);
		session.invalidate(); 
		
		return "redirect:/";  
	}
	
	
	//---------로그인페이지, 회원가입 페이지로 이동---------
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
		
		// 2. DB 조회
		User userEntity = userRepository.mLogin(
											dto.getUsername(), 
											SHA256.encrypt(dto.getPassword(), MyAlgorithm.SHA256)
											);
		
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
	
	
	//--------회원가입 기능---------
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
		
		String encPassword = SHA256.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		
		dto.setPassword(encPassword);
		
		// 2. 정상 - 로그인 페이지
		// User 객체에 데이터를 넣고 User 객체로 받기
		userRepository.save(dto.toEntity());
		
		return Script.href("/loginForm"); //리다이렉션(http상태코드: 300)
	}
}
