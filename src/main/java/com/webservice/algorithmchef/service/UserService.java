package com.webservice.algorithmchef.service;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.webservice.algorithmchef.dto.user.ChangePasswordRequest;
import com.webservice.algorithmchef.dto.user.ChangePasswordResponse;
import com.webservice.algorithmchef.dto.user.FindPasswordRequest;
import com.webservice.algorithmchef.dto.user.FindPasswordResponse;
import com.webservice.algorithmchef.dto.user.FindUserIdRequest;
import com.webservice.algorithmchef.dto.user.FindUserIdResponse;
import com.webservice.algorithmchef.dto.user.UserLoginRequest;
import com.webservice.algorithmchef.dto.user.UserLoginResponse;
import com.webservice.algorithmchef.dto.user.UserSignUpRequest;
import com.webservice.algorithmchef.dto.user.UserSignUpResponse;
import com.webservice.algorithmchef.model.User;
import com.webservice.algorithmchef.repository.UserRepository;
import com.webservice.algorithmchef.util.JwtUtil;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService{
	
	
	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtUtil jwtUtil;
	private final EmailService emailService;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
	    
		return userRepository.findByUserId(username)
	            .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + username));
	}
	
	public UserSignUpResponse signUp(UserSignUpRequest userSignUpRequest) {
		String userId = userSignUpRequest.getUserId();
		String password = userSignUpRequest.getPassword();
		String encodedPassword = passwordEncoder.encode(password);
		String email = userSignUpRequest.getEmail();
		String gender = userSignUpRequest.getGender();
		LocalDateTime birthDate = userSignUpRequest.getBirthDate();
		User user = User.builder()
						.email(email)
						.gender(gender)
						.birthDate(birthDate)
						.userId(userId)
						.password(encodedPassword)
						.role("ROLE_USER")
						.build();
		User newUser = userRepository.save(user);
		return new UserSignUpResponse(newUser);
	}
	
	public UserLoginResponse login(UserLoginRequest userLoginRequest) {
		String userId = userLoginRequest.getUserId();
		String password = userLoginRequest.getPassword();
		User user = userRepository.findByUserId(userId)
					.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		if(!passwordEncoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
		}
		
		String token = jwtUtil.createAccessToken(user.getUserId(),user.getRole(),user.isTemporaryPassword());
		if(user.isTemporaryPassword()) {
			return new UserLoginResponse(token,UserLoginResponse.LoginStatus.FORCE_PASSWORD_CHANGE);
		}else {
			return new UserLoginResponse(token,UserLoginResponse.LoginStatus.SUCCESS);
		}
	}
	
	@Transactional
	public ChangePasswordResponse updateTempPassword(ChangePasswordRequest cRequest,String userId) {
		User user = userRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		String newPassword = cRequest.getPassword();
		String encodedPassword = passwordEncoder.encode(newPassword);
		user.setPassword(encodedPassword);
		user.setTemporaryPassword(false);
		String token = jwtUtil.createAccessToken(user.getUserId(), user.getRole(), user.isTemporaryPassword());
		String message = "임시 비밀번호 변경되었습니다. 메인 화면으로 이동합니다.";
		return new ChangePasswordResponse(token, message);
	}
	
	@Transactional
	public FindUserIdResponse findUserId(FindUserIdRequest findUserIdRequest) {
		String email = findUserIdRequest.getEmail();
		LocalDateTime birthDate = findUserIdRequest.getBirthDate();
		User user = userRepository.findByEmailAndBirthDate(email, birthDate)
					.orElseThrow(() -> new IllegalArgumentException("해당하는 정보와 일치하는 사용자가 없습니다."));
		String userId = user.getUserId();
		return new FindUserIdResponse(userId);
	}
	
	@Transactional
	public FindPasswordResponse findPassword(FindPasswordRequest fPasswordRequest) {
		String userId = fPasswordRequest.getUserId();
		String email = fPasswordRequest.getEmail();
		User user = userRepository.findByUserId(userId)
				.orElseThrow(() -> new IllegalArgumentException("사용자를 찾을 수 없습니다."));
		if(!user.getEmail().equals(email)) {
			throw new IllegalArgumentException("등록된 사용자의 이메일이 아닙니다.");
		}
		String tempPassword = emailService.makeTemporaryPassword();
		String encodedPassword = passwordEncoder.encode(tempPassword);
		user.setPassword(encodedPassword);
		user.setTemporaryPassword(true);
		emailService.sendTemporaryPasswordEmail(email,tempPassword);
		return new FindPasswordResponse(user);
	}
	
	

}
