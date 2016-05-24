package iftttclone.entities;

import java.util.Collection;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "channel")
public class Channel {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;
	@OneToMany(mappedBy = "channel")
	private Collection<Trigger> triggers;
	@OneToMany(mappedBy = "channel")
	private Collection<Action> actions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
