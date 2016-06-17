package iftttclone.controllers;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
	@ResponseStatus(HttpStatus.CREATED)
	public Recipe addRecipe(@RequestBody Recipe recipe) {
		return recipeService.addRecipe(recipe);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Recipe getRecipe(@PathVariable Long id) {
		return recipeService.getRecipe(id);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public Recipe updateRecipe(@PathVariable Long id, @RequestBody Recipe stub) {
		return recipeService.updateRecipe(id, stub);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRecipe(@PathVariable Long id) {
		recipeService.deleteRecipe(id);
	}

	@RequestMapping(value = "/{id}/on", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void turnOn(@PathVariable Long id) {
		recipeService.turnOn(id);
	}

	@RequestMapping(value = "/{id}/off", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void turnOff(@PathVariable Long id) {
		recipeService.turnOff(id);
	}

	@RequestMapping(value = "/{id}/logs", method = RequestMethod.GET)
	public List<RecipeLog> getRecipeLogs(@PathVariable Long id, @RequestParam(value="page", required=false, defaultValue="0") Integer page) {
		return recipeService.getRecipeLogs(id, page);
	}

}
