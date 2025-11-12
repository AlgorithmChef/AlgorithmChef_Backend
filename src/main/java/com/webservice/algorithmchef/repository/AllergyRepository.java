package com.webservice.algorithmchef.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.webservice.algorithmchef.model.Allergy;

public interface AllergyRepository extends JpaRepository<Allergy, Long> {

}
