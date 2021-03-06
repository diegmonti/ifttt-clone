package iftttclone.controllers;

import java.util.List;

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

import iftttclone.entities.PublicRecipe;
import iftttclone.json.JsonViews;
import iftttclone.services.interfaces.PublicRecipeService;

@RestController
@RequestMapping("/publicrecipes")
public class PublicRecipeController {
	@Autowired
	private PublicRecipeService publicRecipeService;

	@JsonView(JsonViews.Summary.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public List<PublicRecipe> getPublicRecipes(
			@RequestParam(value = "search", required = false, defaultValue = "") String search,
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
		return publicRecipeService.getPublicRecipesBySearch(search, page);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public PublicRecipe getPublicRecipe(@PathVariable Long id) {
		return publicRecipeService.getPublicRecipe(id);
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public PublicRecipe addPublicRecipe(@RequestBody PublicRecipe publicRecipe) {
		return publicRecipeService.addPublicRecipe(publicRecipe);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT)
	public PublicRecipe updatePublicRecipe(@PathVariable Long id, @RequestBody PublicRecipe stub) {
		return publicRecipeService.updatePublicRecipe(id, stub);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deletePublicRecipe(@PathVariable Long id) {
		publicRecipeService.deletePublicRecipe(id);
	}

	@RequestMapping(value = "/{id}/add", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void addToFavorite(@PathVariable Long id) {
		publicRecipeService.addToFavorite(id);
	}

	@RequestMapping(value = "/{id}/remove", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void removeFromFavorite(@PathVariable Long id) {
		publicRecipeService.removeFromFavorite(id);
	}

	@JsonView(JsonViews.Summary.class)
	@RequestMapping(value = "/favorite", method = RequestMethod.GET)
	public List<PublicRecipe> getFavoritePublicRecipes(
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
		return publicRecipeService.getFavoritePublicRecipes(page);
	}

	@JsonView(JsonViews.Summary.class)
	@RequestMapping(value = "/published", method = RequestMethod.GET)
	public List<PublicRecipe> getPublishedPublicRecipes(
			@RequestParam(value = "page", required = false, defaultValue = "0") Integer page) {
		return publicRecipeService.getPublishedPublicRecipes(page);
	}

}
