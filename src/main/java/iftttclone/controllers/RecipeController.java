package iftttclone.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import iftttclone.entities.Recipe;
import iftttclone.services.interfaces.RecipeService;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
	@Autowired
	private RecipeService recipeService;
	
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Collection<Recipe> getRecipes() {
		return recipeService.getRecipes();
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public Recipe getRecipe(@PathVariable Long id) {
		return recipeService.getRecipe(id);
	}
}
