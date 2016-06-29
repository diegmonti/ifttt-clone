package iftttclone.entities;

import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import iftttclone.json.TimezoneSerializer;

@Entity
@Table(name = "user")
public class User {
	@JsonIgnore
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String username;

	@JsonProperty(access = Access.WRITE_ONLY)
	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String email;

	@JsonSerialize(using = TimezoneSerializer.class)
	@Column(nullable = false)
	private String timezone;

	@JsonProperty(access = Access.READ_ONLY)
	@OneToMany(mappedBy = "user", fetch = FetchType.EAGER)
	private Set<ChannelConnector> channelConnectors;

	@JsonIgnore
	@ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "public_recipe_favorite", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "public_recipe_id"))
	private Set<PublicRecipe> favoritePublicRecipes;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public Set<ChannelConnector> getChannelConnectors() {
		return channelConnectors;
	}

	public void setChannelConnectors(Set<ChannelConnector> channelConnectors) {
		this.channelConnectors = channelConnectors;
	}

	public Set<PublicRecipe> getFavoritePublicRecipes() {
		return favoritePublicRecipes;
	}

	public void setFavoritePublicRecipes(Set<PublicRecipe> favoritePublicRecipes) {
		this.favoritePublicRecipes = favoritePublicRecipes;
	}

}
