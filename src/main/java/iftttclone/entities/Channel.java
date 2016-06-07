package iftttclone.entities;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "channel")
public class Channel {
	@Id
	private String id;
	@JsonIgnore
	@Column(nullable = false, unique = true)
	private String classpath;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;
	@Transient
	private boolean isConnected;
	@Column(name = "with_connection", nullable = false)
	private boolean isWithConnection;
	@JsonIgnore
	@OneToMany(mappedBy = "channel")
	private Collection<Trigger> triggers;
	@JsonIgnore
	@OneToMany(mappedBy = "channel")
	private Collection<Action> actions;

	public Channel() {
		triggers = new HashSet<Trigger>();
		actions = new HashSet<Action>();
	}

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

	public boolean isConnected() {
		return isConnected;
	}

	public void setConnected(boolean isConnected) {
		this.isConnected = isConnected;
	}

	public boolean isWithConnection() {
		return isWithConnection;
	}

	public void setWithConnection(boolean isWithConnection) {
		this.isWithConnection = isWithConnection;
	}

	public Collection<Trigger> getTriggers() {
		return triggers;
	}

	public void setTriggers(Collection<Trigger> triggers) {
		this.triggers = triggers;
	}

	public Collection<Action> getActions() {
		return actions;
	}

	public void setActions(Collection<Action> actions) {
		this.actions = actions;
	}

}
