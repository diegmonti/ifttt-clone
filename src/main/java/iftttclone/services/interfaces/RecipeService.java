package iftttclone.services.interfaces;

import java.util.Collection;

import org.springframework.security.access.prepost.PreAuthorize;

import iftttclone.entities.Recipe;

public interface RecipeService {

	@PreAuthorize("isAuthenticated()")
	public Collection<Recipe> getRecipes();

	@PreAuthorize("isAuthenticated()")
	public Recipe getRecipe(Long id);

	// TODO
	// public Long createOrUpdateRecipe(Recipe recipe);
	// public void deleteRecipe(Long recipeId);
	// public RecipeLog getRecipeLog(Long recipeId);

}
