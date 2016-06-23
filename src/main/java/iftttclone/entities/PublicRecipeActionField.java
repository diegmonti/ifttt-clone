package iftttclone.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import iftttclone.core.Validator;

@Entity
@Table(name = "public_recipe_action_field")
public class PublicRecipeActionField {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String parameter;

	@Column(nullable = false, length = Validator.MAX_VARCHAR)
	private String value;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "public_recipe_id", nullable = false)
	private PublicRecipe publicRecipe;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public PublicRecipe getPublicRecipe() {
		return publicRecipe;
	}

	public void setPublicRecipe(PublicRecipe publicRecipe) {
		this.publicRecipe = publicRecipe;
	}

}
