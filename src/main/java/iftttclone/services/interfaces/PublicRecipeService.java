package iftttclone.services.interfaces;

import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import iftttclone.entities.PublicRecipe;

public interface PublicRecipeService {

	public Set<PublicRecipe> getPublicRecipes(Integer page);

	public Set<PublicRecipe> getPublicRecipesByName(String name, Integer page);

	public PublicRecipe getPublicRecipe(Long publicRecipeId);

	@PreAuthorize("isAuthenticated()")
	public PublicRecipe addPublicRecipe(PublicRecipe publicRecipe);

	@PreAuthorize("isAuthenticated()")
	public PublicRecipe updatePublicRecipe(PublicRecipe stub);

	@PreAuthorize("isAuthenticated()")
	public void deletePublicRecipe(Long publicRecipeId);

	@PreAuthorize("isAuthenticated()")
	public void addToFavorite(Long publicRecipeId);

	@PreAuthorize("isAuthenticated()")
	public void removeFromFavorite(Long publicRecipeId);

	@PreAuthorize("isAuthenticated()")
	public Set<PublicRecipe> getFavoritePublicRecipes(Integer page);

	@PreAuthorize("isAuthenticated()")
	public Set<PublicRecipe> getPublishedPublicRecipes(Integer page);

}
