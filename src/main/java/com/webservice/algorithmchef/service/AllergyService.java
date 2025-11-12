package com.webservice.algorithmchef.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.webservice.algorithmchef.dto.allergy.AllergyResponse;
import com.webservice.algorithmchef.model.Allergy;
import com.webservice.algorithmchef.repository.AllergyRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AllergyService {
	
	private final AllergyRepository allergyRepository;
	
	public List<AllergyResponse> retrieveAll(){
		List<Allergy> allergies = allergyRepository.findAll();
		List<AllergyResponse> allergyList = allergies.stream().map(allergy ->{
			return new AllergyResponse(allergy);
		}).toList();
		
		return allergyList;
	}

}
