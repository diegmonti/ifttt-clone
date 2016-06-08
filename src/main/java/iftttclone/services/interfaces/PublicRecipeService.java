package iftttclone.services.interfaces;

import java.util.Collection;

import org.springframework.security.access.prepost.PreAuthorize;

import iftttclone.entities.PublicRecipe;

public interface PublicRecipeService {
	// TODO security annotation must be more specific

	public Collection<PublicRecipe> searchRecipes(String name);

	@PreAuthorize("isAuthenticated()")
	public Long createOrUpdateRecipe(PublicRecipe recipe);

	public PublicRecipe getRecipe(Long recipeId);

	@PreAuthorize("isAuthenticated()")
	public void setFavorite(Long recipeId, String username);

	@PreAuthorize("isAuthenticated()")
	public Collection<PublicRecipe> getFavoriteRecipes(Long userId);

	@PreAuthorize("isAuthenticated()")
	public Collection<PublicRecipe> getPublishedRecipes(Long userId);

}
