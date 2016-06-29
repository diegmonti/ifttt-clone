package iftttclone.entities;

import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import iftttclone.json.ActionMapSerializer;
import iftttclone.json.JsonViews;
import iftttclone.json.TimestampSerializer;
import iftttclone.json.TriggerMapSerializer;

@Entity
@Table(name = "channel")
public class Channel {
	@JsonView(JsonViews.Summary.class)
	@Id
	private String id;

	@JsonIgnore
	@Column(nullable = false, unique = true)
	private String classpath;

	@JsonView(JsonViews.Summary.class)
	@Column(nullable = false)
	private String name;

	@JsonView(JsonViews.Summary.class)
	@Column(nullable = false)
	private String description;

	@JsonView(JsonViews.Summary.class)
	@Column(name = "with_connection", nullable = false)
	private boolean withConnection;

	@JsonView(JsonViews.Summary.class)
	@JsonSerialize(using = TimestampSerializer.class)
	@Transient
	private Long connectionTime;

	@JsonView(JsonViews.Summary.class)
	@JsonSerialize(using = TriggerMapSerializer.class)
	@OneToMany(mappedBy = "channel", fetch = FetchType.EAGER)
	@MapKey(name = "method")
	private Map<String, Trigger> triggers;

	@JsonView(JsonViews.Summary.class)
	@JsonSerialize(using = ActionMapSerializer.class)
	@OneToMany(mappedBy = "channel", fetch = FetchType.EAGER)
	@MapKey(name = "method")
	private Map<String, Action> actions;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getClasspath() {
		return classpath;
	}

	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isWithConnection() {
		return withConnection;
	}

	public void setWithConnection(boolean withConnection) {
		this.withConnection = withConnection;
	}

	public Long getConnectionTime() {
		return connectionTime;
	}

	public void setConnectionTime(Long connectionTime) {
		this.connectionTime = connectionTime;
	}

	public Map<String, Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(Map<String, Trigger> triggers) {
		this.triggers = triggers;
	}

	public Map<String, Action> getActions() {
		return actions;
	}

	public void setActions(Map<String, Action> actions) {
		this.actions = actions;
	}

}
