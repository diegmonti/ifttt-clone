package iftttclone.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeLog;
import iftttclone.entities.View;
import iftttclone.services.interfaces.RecipeService;

@RestController
@RequestMapping("/myrecipes")
public class RecipeController {
	@Autowired
	private RecipeService recipeService;
	
	@JsonView(View.Summary.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Set<Recipe> getRecipes() {
		return recipeService.getRecipes();
	}
	
	@RequestMapping(value = "", method = RequestMethod.POST)
	public Recipe addRecipe() {
		return null;
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Recipe getRecipe(@PathVariable Long id) {
		return recipeService.getRecipe(id);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Recipe updateRecipe(@PathVariable Long id) {
		return null;
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	public void deleteRecipe(@PathVariable Long id) {
		recipeService.deleteRecipe(id);
	}
	
	@RequestMapping(value = "/{id}/on", method = RequestMethod.POST)
	public void turnOn(@PathVariable Long id) {
		recipeService.turnOn(id);
	}
	
	@RequestMapping(value = "/{id}/off", method = RequestMethod.POST)
	public void turnOff(@PathVariable Long id) {
		recipeService.turnOff(id);
	}
	
	@RequestMapping(value = "/{id}/publish", method = RequestMethod.POST)
	public void publish(@PathVariable Long id) {
		
	}
	
	@RequestMapping(value = "/{id}/logs", method = RequestMethod.GET)
	public List<RecipeLog> getRecipeLogs(@PathVariable Long id) {
		return recipeService.getRecipeLogs(id, 1);
	}
	
	@RequestMapping(value = "/{id}/logs/{page}", method = RequestMethod.GET)
	public List<RecipeLog> getMoreRecipeLogs(@PathVariable Long id, @PathVariable Integer page) {
		return recipeService.getRecipeLogs(id, page);
	}
}
