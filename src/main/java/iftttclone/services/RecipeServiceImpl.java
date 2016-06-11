package iftttclone.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.Recipe;
import iftttclone.entities.User;
import iftttclone.repositories.RecipeRepository;
import iftttclone.services.interfaces.RecipeService;
import iftttclone.services.interfaces.UserService;

@Component
@Transactional
public class RecipeServiceImpl implements RecipeService {
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private UserService userService;

	@Override
	public Collection<Recipe> getRecipes() {
		User user = userService.getUser();
		return recipeRepository.findRecipeByUser(user);
	}

	@Override
	public Recipe getRecipe(Long id) {
		User user = userService.getUser();
		Recipe recipe = recipeRepository.findRecipeByIdAndUser(id, user);
		if (recipe == null) {
			throw new SecurityException();
		}
		return recipe;
	}

}
