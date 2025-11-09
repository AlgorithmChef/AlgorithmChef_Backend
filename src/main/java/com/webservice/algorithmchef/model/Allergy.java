package com.webservice.algorithmchef.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Builder
@ToString
@Getter
@Setter
public class Allergy {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="allery_id")
	private long id;
	
	@Column(nullable = false,unique = true)
	private String name;

	@OneToMany(mappedBy = "allergy",cascade = CascadeType.ALL,orphanRemoval = true)
	private List<UserAllergy> allergyList;
}
