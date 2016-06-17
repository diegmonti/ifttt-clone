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

@Entity
@Table(name = "recipe_log")
public class RecipeLog {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "recipe_id", nullable = false)
	private Recipe recipe;

	@Column(nullable = false)
	private Long timestamp;

	@Column(nullable = false)
	private RecipeLogEvent event;

	public RecipeLog() {
		// Default constructor
	}

	public RecipeLog(Recipe recipe, RecipeLogEvent event) {
		this.recipe = recipe;
		this.timestamp = System.currentTimeMillis();
		this.event = event;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public RecipeLogEvent getEvent() {
		return event;
	}

	public void setEvent(RecipeLogEvent event) {
		this.event = event;
	}

}
