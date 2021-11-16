package com.cos.blogapp.web;


import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.service.CommentService;
import com.cos.blogapp.web.dto.CMRespDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor // final이 붙은 필드에 대한 생성자가 만들어진다(DI)
@Controller // 컴포넌트 스캔(스프링) IoC
public class CommentController {
	
	private final CommentService commentService;
	private final HttpSession session;
	
	
	// 댓글 삭제-------------------------------------------------------------
	@DeleteMapping("/api/comment/{id}")
	public @ResponseBody CMRespDto<?> deleteById(@PathVariable int id) {
		
		// 인증
		User principal = (User) session.getAttribute("principal");

		commentService.댓글삭제(id, principal);
		
		return new CMRespDto<>(1, "성공", null);
	}
}
