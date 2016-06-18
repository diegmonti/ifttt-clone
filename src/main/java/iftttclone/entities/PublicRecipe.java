package iftttclone.entities;

import java.util.Map;

import javax.persistence.CascadeType;
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

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import iftttclone.json.ActionDeserializer;
import iftttclone.json.ActionSerializer;
import iftttclone.json.JsonViews;
import iftttclone.json.TriggerDeserializer;
import iftttclone.json.TriggerSerializer;
import iftttclone.json.UserSerializer;

@Entity
@Table(name = "public_recipe")
public class PublicRecipe {
	@JsonView(JsonViews.Summary.class)
	@JsonProperty(access = Access.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonView(JsonViews.Summary.class)
	@Column(nullable = false)
	private String title;

	@JsonView(JsonViews.Summary.class)
	@Column(nullable = false)
	private String description;

	@JsonSerialize(using = TriggerSerializer.class)
	@JsonDeserialize(using = TriggerDeserializer.class)
	@ManyToOne
	@JoinColumn(name = "trigger_id", nullable = false)
	private Trigger trigger;

	@OneToMany(mappedBy = "publicRecipe", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "parameter")
	private Map<String, PublicRecipeTriggerField> publicRecipeTriggerFields;

	@JsonSerialize(using = ActionSerializer.class)
	@JsonDeserialize(using = ActionDeserializer.class)
	@ManyToOne
	@JoinColumn(name = "action_id", nullable = false)
	private Action action;

	@OneToMany(mappedBy = "publicRecipe", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "parameter")
	private Map<String, PublicRecipeActionField> publicRecipeActionFields;

	@JsonView(JsonViews.Summary.class)
	@JsonSerialize(using = UserSerializer.class)
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JsonView(JsonViews.Summary.class)
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

	public Integer getFavorites() {
		return favorites;
	}

	public void setFavorites(Integer favorites) {
		this.favorites = favorites;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PublicRecipe other = (PublicRecipe) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

}
