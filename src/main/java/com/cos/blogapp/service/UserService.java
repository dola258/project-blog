package com.cos.blogapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.domain.user.UserRepository;
import com.cos.blogapp.handler.exception.MyAsyncNotFoundException;
import com.cos.blogapp.handler.exception.MyNotFoundException;
import com.cos.blogapp.util.MyAlgorithm;
import com.cos.blogapp.util.SHA256;
import com.cos.blogapp.web.dto.JoinReqDto;
import com.cos.blogapp.web.dto.LoginReqDto;
import com.cos.blogapp.web.dto.UserUpdateDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class UserService {
	
	private final UserRepository userRepository;
	
	// 이건 하나의 서비스인가? (principal 값 변경, update, 세션값 변경(리퀘스트 관련이라서 컨트롤러))

	@Transactional(rollbackFor = MyAsyncNotFoundException.class) 
	public void 회원수정(User principal, UserUpdateDto dto) {

		principal.setEmail(dto.getEmail());
		userRepository.save(principal);
	}


	public User 로그인(LoginReqDto dto) {
		// 2. DB 조회
		return  userRepository.mLogin( dto.getUsername(), SHA256.encrypt(dto.getPassword(), MyAlgorithm.SHA256)	);
	}

	@Transactional(rollbackFor = MyNotFoundException.class) 
	public void 회원가입(JoinReqDto dto) {
		String encPassword = SHA256.encrypt(dto.getPassword(), MyAlgorithm.SHA256);
		
		dto.setPassword(encPassword);
		
		// 2. 정상 - 로그인 페이지
		// User 객체에 데이터를 넣고 User 객체로 받기
		userRepository.save(dto.toEntity());
		
	}
}
