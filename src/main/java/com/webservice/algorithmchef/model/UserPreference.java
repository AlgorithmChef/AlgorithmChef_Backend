package com.webservice.algorithmchef.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class UserPreference {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="user_preference_id")
	private Long id;
	
	@Column
	private String disLikedIngredients;
	
	@Column
	private String likedIngredients;
	
	@Column
	private String preferredCuisine;
	
	@Column(nullable = false, columnDefinition = "varchar(255) default '보통'")
	private String spiceLevel;
	
	@Column(nullable = false,columnDefinition = "boolean default true")
	private boolean  wantsRegisterNudge;
	
	@Column(nullable = false,columnDefinition = "boolean default true")
	private boolean allowPushConsumption;
	
	@Column(nullable = false,columnDefinition = "boolean default true")
	private boolean allowPushComment;
	
	@Column(nullable = false,columnDefinition = "boolean default true")
	private boolean allowPushNudge;
	
	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="user_id", unique = true)
	private User user;
	
}
