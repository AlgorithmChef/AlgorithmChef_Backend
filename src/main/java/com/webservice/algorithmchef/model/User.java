package com.webservice.algorithmchef.model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

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
public class User implements UserDetails  {
	
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
	private LocalDateTime birthDate;
	
	@Column(nullable = false, columnDefinition = "varchar(255) default 'ROLE_USER'")
    private String role;
	
	@Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
	private boolean isTemporaryPassword;
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
	@ToString.Exclude
	private List<UserHealthGoal> userHealthGoals;
	
	@OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
	@ToString.Exclude
	private List<UserAllergy> userAllergyList;
	
	@OneToOne(mappedBy="user",cascade = CascadeType.ALL,orphanRemoval = true)
	@ToString.Exclude
	private UserPreference userPreference;
	
	@OneToMany(mappedBy="user",cascade = CascadeType.ALL,orphanRemoval = true)
	@ToString.Exclude
	private List<UserFridge> userFridge;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		// TODO Auto-generated method stub
		return Collections.singletonList(new SimpleGrantedAuthority(this.role));
	}

	@Override
	public String getUsername() {
		// TODO Auto-generated method stub
		return this.userId;
	}
}
