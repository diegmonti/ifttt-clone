package iftttclone.entities;

import java.util.Collection;
import java.util.HashSet;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "channel_action")
public class Action {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false, unique = true)
	private String method;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;
	@ManyToOne
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;
	@OneToMany(mappedBy = "action")
	private Collection<ActionField> actionFields;
	
	public Action() {
		actionFields = new HashSet<ActionField>();
	}

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

	public Collection<ActionField> getActionFields() {
		return actionFields;
	}

	public void setActionFields(Collection<ActionField> actionFields) {
		this.actionFields = actionFields;
	}

}
