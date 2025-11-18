package com.webservice.algorithmchef.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.Fridge;

public interface FridgeRepository extends JpaRepository<Fridge, Long> {

	Optional<Fridge> findByNameAndUser_UserId(String name, String userId);
}
