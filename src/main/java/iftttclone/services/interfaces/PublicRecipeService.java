package iftttclone.services.interfaces;

import java.util.Collection;

import iftttclone.entities.PublicRecipe;

public interface PublicRecipeService {

	public Collection<PublicRecipe> searchRecipes(String name);
	public Long addRecipe(PublicRecipe recipe);
	public PublicRecipe getRecipe(Long recipeId);
	public void setFavorite(Long recipeId, String username);
	public Collection<PublicRecipe> getFavoriteRecipes(String username);
	public Collection<PublicRecipe> getPublishedRecipes(String username);
}
