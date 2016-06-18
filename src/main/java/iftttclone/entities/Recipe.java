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
import javax.persistence.ManyToOne;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import iftttclone.utils.JsonViews;
import iftttclone.utils.TimezoneSerializer;

@Entity
@Table(name = "recipe")
public class Recipe {
	@JsonView(JsonViews.Summary.class)
	@JsonProperty(access = Access.READ_ONLY)
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonView(JsonViews.Summary.class)
	@Column(nullable = false)
	private String title;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "trigger_id", nullable = false)
	private Trigger trigger;

	@Transient
	private String triggerChannelId;

	@Transient
	private String triggerMethod;

	@OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "parameter")
	private Map<String, RecipeTriggerField> recipeTriggerFields;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "action_id", nullable = false)
	private Action action;

	@Transient
	private String actionChannelId;

	@Transient
	private String actionMethod;

	@OneToMany(mappedBy = "recipe", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@MapKey(name = "parameter")
	private Map<String, RecipeActionField> recipeActionFields;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@JsonView(JsonViews.Summary.class)
	@Column(nullable = false)
	private boolean active;

	@JsonView(JsonViews.Summary.class)
	@JsonProperty(access = Access.READ_ONLY)
	@JsonSerialize(using = TimezoneSerializer.class)
	@Column(name = "creation_time", nullable = false)
	private Long creationTime;

	@JsonView(JsonViews.Summary.class)
	@JsonProperty(access = Access.READ_ONLY)
	@JsonSerialize(using = TimezoneSerializer.class)
	@Column(name = "last_run", nullable = false)
	private Long lastRun;

	@JsonView(JsonViews.Summary.class)
	@JsonProperty(access = Access.READ_ONLY)
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

	public String getTriggerChannelId() {
		return triggerChannelId;
	}

	public void setTriggerChannelId(String triggerChannelId) {
		this.triggerChannelId = triggerChannelId;
	}

	public String getTriggerMethod() {
		return triggerMethod;
	}

	public void setTriggerMethod(String triggerMethod) {
		this.triggerMethod = triggerMethod;
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

	public String getActionChannelId() {
		return actionChannelId;
	}

	public void setActionChannelId(String actionChannelId) {
		this.actionChannelId = actionChannelId;
	}

	public String getActionMethod() {
		return actionMethod;
	}

	public void setActionMethod(String actionMethod) {
		this.actionMethod = actionMethod;
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

	public Long getCreationTime() {
		return creationTime;
	}

	public void setCreationTime(Long creationTime) {
		this.creationTime = creationTime;
	}

	public Long getLastRun() {
		return lastRun;
	}

	public void setLastRun(Long lastRun) {
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
