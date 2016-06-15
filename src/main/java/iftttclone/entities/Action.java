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

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "channel_action")
public class Action {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@JsonIgnore
	@Column(nullable = false)
	private String method;

	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String description;

	@JsonIgnore
	@ManyToOne
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;

	@OneToMany(mappedBy = "action", fetch = FetchType.EAGER)
	@MapKey(name = "parameter")
	private Map<String, ActionField> actionFields;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
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

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Map<String, ActionField> getActionFields() {
		return actionFields;
	}

	public void setActionFields(Map<String, ActionField> actionFields) {
		this.actionFields = actionFields;
	}

}
