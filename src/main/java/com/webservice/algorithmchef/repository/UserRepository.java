package com.webservice.algorithmchef.repository;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.webservice.algorithmchef.model.User;

public interface UserRepository extends JpaRepository<User, Long> {
	
	public Optional<User> findByUserId(String userId);
	public Optional<User> findByEmailAndBirthDate(String email,LocalDateTime birthDate);
	@Query("SELECT u FROM User u LEFT JOIN FETCH u.userPreference WHERE u.userId = :userId")
    Optional<User> findByUserIdWithPreference(@Param("userId") String userId);

}
