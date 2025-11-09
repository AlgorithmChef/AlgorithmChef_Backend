package com.webservice.algorithmchef.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.OneToMany;
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
public class HealthGoal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="goal_id")
	private Long id;
	
	@Column(nullable=false)
	private String name;
	
	@Column(nullable = false)
	private String category;
	
	@Lob
	@Column(nullable = false)
	private String description;
	
	@OneToMany(mappedBy = "healthGoal",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<UserHealthGoal> userHealthGoals;
}
