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

import iftttclone.core.Validator.FieldType;

@Entity
@Table(name = "channel_trigger_field")
public class TriggerField {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@Column(nullable = false)
	private String parameter;

	@Column(nullable = false)
	private boolean publishable;

	@Column(nullable = false)
	private FieldType type;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "channel_trigger_id", nullable = false)
	private Trigger trigger;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getParameter() {
		return parameter;
	}

	public void setParameter(String parameter) {
		this.parameter = parameter;
	}

	public boolean isPublishable() {
		return publishable;
	}

	public void setPublishable(boolean publishable) {
		this.publishable = publishable;
	}

	public FieldType getType() {
		return type;
	}

	public void setType(FieldType type) {
		this.type = type;
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

	public Trigger getTrigger() {
		return trigger;
	}

	public void setTrigger(Trigger trigger) {
		this.trigger = trigger;
	}

}
