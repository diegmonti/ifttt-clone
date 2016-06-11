package iftttclone.entities;

import java.util.Collection;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "channel_trigger")
public class Trigger {
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

	@OneToMany(mappedBy = "trigger", fetch = FetchType.EAGER)
	private Set<TriggerField> triggerFields;

	@OneToMany(mappedBy = "trigger", fetch = FetchType.EAGER)
	private Set<Ingredient> ingredients;

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

	public Collection<TriggerField> getTriggerFields() {
		return triggerFields;
	}

	public void setTriggerFields(Set<TriggerField> triggerFields) {
		this.triggerFields = triggerFields;
	}

	public Collection<Ingredient> getIngredients() {
		return ingredients;
	}

	public void setIngredients(Set<Ingredient> ingredients) {
		this.ingredients = ingredients;
	}

}
