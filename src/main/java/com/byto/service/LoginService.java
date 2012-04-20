package com.byto.service;

import java.io.File;
import java.security.MessageDigest;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Component;

import com.byto.util.BytoUtils;
import com.byto.vo.Login;
import com.thoughtworks.xstream.XStream;

@Component
public class LoginService {

	// private File sessionFile = BytoUtils.getFileFromProps("LOGIN");

	/** 로그인처리 */
	public void processLogin(HttpSession session, String id, String passwd)
			throws Exception {
		StringBuilder idStringBuilder = new StringBuilder();
		StringBuilder passwdStringBuilder = new StringBuilder();
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		
		//ID
		md5.update(id.getBytes());
		byte[] md5IdBytes = md5.digest();
		
		for(int i=0; i < md5IdBytes.length; i++) {
			idStringBuilder.append(Integer.toHexString(0xFF & md5IdBytes[i]));
		}
		String md5Id = idStringBuilder.toString();
		
		
		//Passwd
		md5.update(passwd.getBytes());
		byte[] md5PasswdBytes = md5.digest();
		
		for(int i=0; i < md5PasswdBytes.length; i++) {
			passwdStringBuilder.append(Integer.toHexString((int) md5PasswdBytes[i] & 0xFF));
		}
		String md5Passwd = passwdStringBuilder.toString();
		
		String dbId 	= "a6109bb8835a8c4dcdbb38ddfbb14a17";
		String dbPasswd = "36129c6aff7fa46a505929fd638961";
		                 //36129c6a0f0f7fa46a505929fd638961 실제 값은 이렇게 나와야 하나 255를 비트연산&를 통과시키니 이상한 반응
						   
		if (id == null || "".equals(id))
			throw new Exception("아이디를 입력해주세요");
		if (passwd == null || "".equals(passwd))
			throw new Exception("비밀번호를 입력해주세요");

		if (dbId.equals(md5Id) && dbPasswd.equals(md5Passwd)) {
			session.setAttribute("login", "yes");
		} else {
			throw new Exception("아이디나 패스워드가 일치하지 않습니다.");
		}
	}

	/** 로그아웃 처리 */
	public void processLogout(HttpSession session) throws Exception {
		session.invalidate();
	}
}