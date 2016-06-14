package iftttclone.entities;

import java.util.Date;
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
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;

@Entity
@Table(name = "recipe")
public class Recipe {
	@JsonView(View.Summary.class)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonView(View.Summary.class)
	@Column(nullable = false)
	private String title;

	@ManyToOne
	@JoinColumn(name = "trigger_id", nullable = false)
	private Trigger trigger;

	@OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "parameter")
	private Map<String, RecipeTriggerField> recipeTriggerFields;

	@ManyToOne
	@JoinColumn(name = "action_id", nullable = false)
	private Action action;

	@OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "parameter")
	private Map<String, RecipeActionField> recipeActionFields;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JsonView(View.Summary.class)
	@Column(nullable = false)
	private boolean active;

	@JsonView(View.Summary.class)
	@Column(name = "creation_time", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationTime;

	@JsonView(View.Summary.class)
	@Column(name = "last_run", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastRun;

	@JsonView(View.Summary.class)
	@Column(nullable = false)
	private Integer runs;

	@JsonIgnore
	@OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL)
	private Set<RecipeLog> logs;

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

	public Set<RecipeLog> getLogs() {
		return logs;
	}

	public void setLogs(Set<RecipeLog> logs) {
		this.logs = logs;
	}

}
