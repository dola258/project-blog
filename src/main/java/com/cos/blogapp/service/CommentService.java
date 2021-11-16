package com.cos.blogapp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cos.blogapp.domain.board.Board;
import com.cos.blogapp.domain.board.BoardRepository;
import com.cos.blogapp.domain.comment.Comment;
import com.cos.blogapp.domain.comment.CommentRepository;
import com.cos.blogapp.domain.user.User;
import com.cos.blogapp.handler.exception.MyAsyncNotFoundException;
import com.cos.blogapp.handler.exception.MyNotFoundException;
import com.cos.blogapp.web.dto.CommentSaveDto;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class CommentService {
	
	private final CommentRepository commentRepository;
	private final BoardRepository boardRepository;
	
	@Transactional(rollbackFor = MyNotFoundException.class) 
	public void 댓글등록(int boardId, CommentSaveDto dto, User principal) {

		Board boardEntity = boardRepository.findById(boardId)
				.orElseThrow(() -> new MyNotFoundException("해당 게시글을 찾을 수 없습니다."));
		
		Comment comment = new Comment();
		
		comment.setContent(dto.getContent());
		comment.setUser(principal);
		comment.setBoard(boardEntity);
		
		// 4. save하기
		commentRepository.save(comment);
	}
	
	@Transactional(rollbackFor = MyAsyncNotFoundException.class) 
	public void 댓글삭제(int id, User principal) {

		// 영속성 컨텍스트
		Comment commentEntity = commentRepository.findById(id)
				.orElseThrow(() -> new MyAsyncNotFoundException("없는 댓글번호입니다."));
		
		if(principal.getId() != commentEntity.getUser().getId() ) {
			throw new MyAsyncNotFoundException("해당 댓글을 삭제할 수 없는 유저입니다.");
		}
		
		commentRepository.deleteById(id);
	}
}
