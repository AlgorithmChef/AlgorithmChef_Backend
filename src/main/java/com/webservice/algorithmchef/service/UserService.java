package com.webservice.algorithmchef.service;

import java.time.LocalDateTime;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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

}
