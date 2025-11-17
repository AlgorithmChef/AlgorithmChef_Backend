package com.webservice.algorithmchef.model;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Fridge {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name="fridge_id")
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id")
	private User user;
	
	@Column(nullable = false, columnDefinition = "varchar(255) default '나의 냉장고'")
	private String name;
	
	@OneToMany(mappedBy = "fridge",cascade = CascadeType.ALL, orphanRemoval=true)
	private List<FridgeIngredient> ingredients;
}
