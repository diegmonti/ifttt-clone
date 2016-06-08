package iftttclone.entities;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "public_recipe")
public class PublicRecipe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String title;
	@Column(nullable = false)
	private String description;
	@ManyToOne
	@JoinColumn(name = "trigger_id", nullable = false)
	private Trigger trigger;
	@OneToMany(mappedBy = "publicRecipe", fetch = FetchType.EAGER)
	@MapKey(name = "parameter")
	private Map<String, PublicRecipeTriggerField> publicRecipeTriggerFields;
	@ManyToOne
	@JoinColumn(name = "action_id", nullable = false)
	private Action action;
	@OneToMany(mappedBy = "publicRecipe", fetch = FetchType.EAGER)
	@MapKey(name = "parameter")
	private Map<String, PublicRecipeActionField> publicRecipeActionFields;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	@Column(nullable = false)
	private Integer adds;
	@Column(nullable = false)
	private Integer favorites;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public Map<String, PublicRecipeTriggerField> getPublicRecipeTriggerFields() {
		return publicRecipeTriggerFields;
	}

	public void setPublicRecipeTriggerFields(Map<String, PublicRecipeTriggerField> publicRecipeTriggerFields) {
		this.publicRecipeTriggerFields = publicRecipeTriggerFields;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Map<String, PublicRecipeActionField> getPublicRecipeActionFields() {
		return publicRecipeActionFields;
	}

	public void setPublicRecipeActionFields(Map<String, PublicRecipeActionField> publicRecipeActionFields) {
		this.publicRecipeActionFields = publicRecipeActionFields;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Integer getAdds() {
		return adds;
	}

	public void setAdds(Integer adds) {
		this.adds = adds;
	}

	public Integer getFavorites() {
		return favorites;
	}

	public void setFavorites(Integer favorites) {
		this.favorites = favorites;
	}

}
