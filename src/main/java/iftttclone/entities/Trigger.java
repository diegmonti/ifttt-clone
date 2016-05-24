package iftttclone.entities;

import java.util.Collection;

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
@Table(name = "channel_trigger")
public class Trigger {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String name;
	@Column(nullable = false)
	private String description;
	@ManyToOne
	@JoinColumn(name = "channel_id", nullable = false)
	private Channel channel;
	@OneToMany(mappedBy = "trigger")
	private Collection<TriggerField> triggerFields;

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

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public Collection<TriggerField> getTriggerFields() {
		return triggerFields;
	}

	public void setTriggerFields(Collection<TriggerField> triggerFields) {
		this.triggerFields = triggerFields;
	}

}
