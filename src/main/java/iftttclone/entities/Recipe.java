package iftttclone.entities;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name = "recipe")
public class Recipe {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String title;
	@ManyToOne
	@JoinColumn(name = "trigger_id", nullable = false)
	private Trigger trigger;
	@OneToMany(mappedBy = "recipe")
	@MapKey(name = "name")
	private Map<String, RecipeTriggerField> recipeTriggerFields;
	@ManyToOne
	@JoinColumn(name = "action_id", nullable = false)
	private Action action;
	@OneToMany(mappedBy = "recipe")
	@MapKey(name = "name")
	private Map<String, RecipeActionField> recipeActionFields;
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;
	@Column(nullable = false)
	private boolean active;
	@Column(name = "creation_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;
	@Column(name = "last_run")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastRun;
	@Column(nullable = false)
	private Integer runs;
	@OneToMany(mappedBy = "recipe")
	private Collection<RecipeLog> recipeLogs;

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

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

	public Map<String, RecipeTriggerField> getRecipeTriggerFields() {
		return recipeTriggerFields;
	}

	public void setRecipeTriggerFields(Map<String, RecipeTriggerField> recipeTriggerFields) {
		this.recipeTriggerFields = recipeTriggerFields;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

	public Map<String, RecipeActionField> getRecipeActionFields() {
		return recipeActionFields;
	}

	public void setRecipeActionFields(Map<String, RecipeActionField> recipeActionFields) {
		this.recipeActionFields = recipeActionFields;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Date creationTime) {
		this.creationTime = creationTime;
	}

	public Date getLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public Integer getRuns() {
		return runs;
	}

	public void setRuns(Integer runs) {
		this.runs = runs;
	}

	public Collection<RecipeLog> getRecipeLogs() {
		return recipeLogs;
	}

	public void setRecipeLogs(Collection<RecipeLog> recipeLogs) {
		this.recipeLogs = recipeLogs;
	}

}
