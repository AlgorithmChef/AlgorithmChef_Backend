package com.webservice.algorithmchef.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	public Optional<User> findByUserId(String userId);
	public Optional<User> findByEmailAndBirthDate(String email,LocalDateTime birthDate);

}
