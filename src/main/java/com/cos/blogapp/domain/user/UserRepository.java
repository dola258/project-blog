package com.cos.blogapp.domain.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

// save() 인서트, 업데이트 (id가 같으면 update, id가 없으면 insert)
// findById() 한 건 셀렉트
// findAll() 전체 셀렉트
// deleteById() 한 건 삭제

// DAO
// @Repository
public interface UserRepository extends JpaRepository<User, Integer>{
	
	@Query(value = "insert into user(username, password, email) values (:username, :password, :email)", nativeQuery = true)
	void join(String username, String password, String email);
	
	@Query(value = "select * from user where username = :username and password = :password", nativeQuery = true)
	User mLogin(String username, String password);
}
