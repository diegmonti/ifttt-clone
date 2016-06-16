package iftttclone.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/*import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;

import iftttclone.entities.PublicRecipe;
import iftttclone.entities.Recipe;*/


@RestController
@RequestMapping("/publicrecipes")
public class PublicRecipeController {
	//@Autowired
	//private PublicRecipeService publicRecipeService;
	
	/*@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public PublicRecipe addRecipe(@RequestBody Recipe recipe) {
		return publicRecipeService.addRecipe(recipe);
	}
	
	@RequestMapping(value="", method=RequestMethod.GET)
	public List<PublicRecipe> getRecipes(@RequestParam(value="page", required=false, defaultValue="1") Integer page){
		return publicRecipeService.getRecipes(page);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteRecipe(@PathVariable Long id) {
		publicRecipeService.deleteRecipe(id);
	}
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public PublicRecipe getRecipe(@PathVariable Long id) {
		return publicRecipeService.getRecipe(id);
	}*/

}
