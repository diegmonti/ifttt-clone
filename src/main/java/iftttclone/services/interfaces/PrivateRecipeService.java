package iftttclone.services.interfaces;

import java.util.Collection;

import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeLog;

public interface PrivateRecipeService {

	public Collection<Recipe> getRecipes(String username);
	public Recipe getRecipe(String username, Long recipeId);
	
	public Long addRecipe(Recipe recipe); // we can also use this method to modify
	public void deleteRecipe(Long recipeId);
	public RecipeLog getRecipeLog(Long recipeId);
	
}
