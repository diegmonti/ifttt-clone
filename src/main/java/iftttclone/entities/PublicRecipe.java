package iftttclone.entities;

import java.util.Map;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

	@JsonView(JsonViews.Summary.class)
	@JsonSerialize(using = TriggerSerializer.class)
	@JsonDeserialize(using = TriggerDeserializer.class)
	@ManyToOne
	@JoinColumn(name = "trigger_id", nullable = false)
	private Trigger trigger;

	@OneToMany(mappedBy = "publicRecipe", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "parameter")
	private Map<String, PublicRecipeTriggerField> publicRecipeTriggerFields;

	@JsonView(JsonViews.Summary.class)
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

	@JsonIgnore
	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "public_recipe_favorite", joinColumns = @JoinColumn(name = "public_recipe_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
	private Set<User> favoriteByUser;

	@JsonView(JsonViews.Summary.class)
	@Transient
	private boolean favorite;

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

	public Set<User> getFavoriteByUser() {
		return favoriteByUser;
	}

	public void setFavoriteByUser(Set<User> favoriteByUser) {
		this.favoriteByUser = favoriteByUser;
	}

	public boolean isFavorite() {
		return favorite;
	}

	public void setFavorite(boolean favorite) {
		this.favorite = favorite;
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
