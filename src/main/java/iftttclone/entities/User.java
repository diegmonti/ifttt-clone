package iftttclone.entities;

import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "user")
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(nullable = false)
	private String username;
	@Column(nullable = false)
	private String password;
	@Column(nullable = false)
	private String email;
	@Column(nullable = false)
	private String timezone;
	@OneToMany(mappedBy = "user")
	private Collection<ChannelConnector> connectedChannels;
	@OneToMany(mappedBy = "user")
	private Collection<Recipe> recipes;
	@OneToMany(mappedBy = "user")
	private Collection<PublicRecipe> publicRecipes;
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(name = "public_recipe_favorite", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "public_recipe_id"))
	private Collection<PublicRecipe> favoritePublicRecipes;

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

	public Collection<ChannelConnector> getConnectedChannels() {
		return connectedChannels;
	}

	public void setConnectedChannels(Collection<ChannelConnector> connectedChannels) {
		this.connectedChannels = connectedChannels;
	}

	public Collection<Recipe> getRecipes() {
		return recipes;
	}

	public void setRecipes(Collection<Recipe> recipes) {
		this.recipes = recipes;
	}

	public Collection<PublicRecipe> getPublicRecipes() {
		return publicRecipes;
	}

	public void setPublicRecipes(Collection<PublicRecipe> publicRecipes) {
		this.publicRecipes = publicRecipes;
	}

	public Collection<PublicRecipe> getFavoritePublicRecipes() {
		return favoritePublicRecipes;
	}

	public void setFavoritePublicRecipes(Collection<PublicRecipe> favoritePublicRecipes) {
		this.favoritePublicRecipes = favoritePublicRecipes;
	}

}
