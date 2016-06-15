package iftttclone.services.interfaces;

import java.util.List;
import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeLog;

@PreAuthorize("isAuthenticated()")
public interface RecipeService {

	public Set<Recipe> getRecipes();

	public Recipe getRecipe(Long id);

	public Recipe addRecipe(Recipe recipe);

	public Recipe updateRecipe(Recipe stub);

	public void deleteRecipe(Long id);

	public void turnOn(Long id);

	public void turnOff(Long id);

	public void publish(Long id);

	public List<RecipeLog> getRecipeLogs(Long id, Integer page);

}
