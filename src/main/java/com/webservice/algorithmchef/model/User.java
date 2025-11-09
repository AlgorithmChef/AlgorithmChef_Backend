package com.webservice.algorithmchef.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_id")
	private Long id;
	
	@Column(nullable = false,unique = true,name = "user_login_id")
	private String userId;
	
	@Column(nullable = false)
	private String password;
	
	@Column(nullable = false, unique = true)
	@Email
	private String email;
	
	@CreationTimestamp
	private LocalDateTime createdAt;
	
	@Column(nullable=false)
	private String gender;
	
	@Column(nullable=false)
	private LocalDate birthDate;
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
	@ToString.Exclude
	private List<UserHealthGoal> userHealthGoals;
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
	@ToString.Exclude
	private List<UserAllergy> userAllergyList;
	
	@OneToMany(mappedBy="user",cascade = CascadeType.ALL,orphanRemoval = true)
	@ToString.Exclude
	private List<UserPreference> userPreferenceList;
	
	@OneToOne(mappedBy="user",cascade = CascadeType.ALL,orphanRemoval = true)
	@ToString.Exclude
	private UserFridge userFridge;
}
