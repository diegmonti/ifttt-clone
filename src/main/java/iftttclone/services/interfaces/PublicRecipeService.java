package iftttclone.services.interfaces;

import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import iftttclone.entities.PublicRecipe;

public interface PublicRecipeService {

	public List<PublicRecipe> getPublicRecipes(Integer page);

	public List<PublicRecipe> getPublicRecipesByTitle(String title, Integer page);

	public PublicRecipe getPublicRecipe(Long publicRecipeId);

	@PreAuthorize("isAuthenticated()")
	public PublicRecipe addPublicRecipe(PublicRecipe publicRecipe);

	@PreAuthorize("isAuthenticated()")
	public PublicRecipe updatePublicRecipe(Long publicRecipeId, PublicRecipe stub);

	@PreAuthorize("isAuthenticated()")
	public void deletePublicRecipe(Long publicRecipeId);

	@PreAuthorize("isAuthenticated()")
	public void addToFavorite(Long publicRecipeId);

	@PreAuthorize("isAuthenticated()")
	public void removeFromFavorite(Long publicRecipeId);

	@PreAuthorize("isAuthenticated()")
	public Set<PublicRecipe> getFavoritePublicRecipes();

	@PreAuthorize("isAuthenticated()")
	public List<PublicRecipe> getPublishedPublicRecipes(Integer page);

}
