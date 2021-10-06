package com.cos.blogapp.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SHA256 {
	
	public static void encrypt(String rawPassword) throws NoSuchAlgorithmException {
		// 1. SHA256 함수를 가진 클래스 객체 가져오기
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		// 2. 비밀번호 1234 -> SHA256한테 던지기
		md.update(rawPassword.getBytes());
		
		StringBuilder sb = new StringBuilder();
		
		System.out.println(rawPassword.getBytes());
		System.out.println();
		System.out.println(md.digest());
	}
}
