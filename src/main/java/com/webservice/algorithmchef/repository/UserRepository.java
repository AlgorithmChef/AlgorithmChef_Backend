package com.webservice.algorithmchef.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	public Optional<User> findByUserId(String userId);

}
