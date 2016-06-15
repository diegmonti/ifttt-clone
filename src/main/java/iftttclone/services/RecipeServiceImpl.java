package iftttclone.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeLog;
import iftttclone.entities.RecipeLogEvent;
import iftttclone.entities.User;
import iftttclone.exceptions.InvalidRequestException;
import iftttclone.repositories.RecipeLogRepository;
import iftttclone.repositories.RecipeRepository;
import iftttclone.services.interfaces.RecipeService;
import iftttclone.services.interfaces.UserService;

@Component
@Transactional
public class RecipeServiceImpl implements RecipeService {
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private RecipeLogRepository recipeLogRepository;
	@Autowired
	private UserService userService;

	@Override
	public Set<Recipe> getRecipes() {
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
		recipe.setTriggerChannelId(recipe.getTrigger().getChannel().getId());
		recipe.setTriggerMethod(recipe.getTrigger().getMethod());
		recipe.setActionChannelId(recipe.getAction().getChannel().getId());
		recipe.setActionMethod(recipe.getAction().getMethod());
		return recipe;
	}

	@Override
	public Recipe addRecipe(Recipe recipe) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Recipe updateRecipe(Recipe stub) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteRecipe(Long id) {
		User user = userService.getUser();
		Recipe recipe = recipeRepository.findRecipeByIdAndUser(id, user);
		if (recipe == null) {
			throw new SecurityException();
		}
		recipeRepository.delete(recipe);
	}

	@Override
	public void turnOn(Long id) {
		User user = userService.getUser();
		Recipe recipe = recipeRepository.findRecipeByIdAndUser(id, user);
		if (recipe == null) {
			throw new SecurityException();
		}

		// Check if it is already active
		if (recipe.isActive()) {
			throw new InvalidRequestException("The recipe is already active");
		}

		// Add log entry
		RecipeLog recipeLog = new RecipeLog(recipe, RecipeLogEvent.ACTIVE);
		recipeLogRepository.save(recipeLog);

		// Turn on
		recipe.setActive(true);
		recipeRepository.save(recipe);
	}

	@Override
	public void turnOff(Long id) {
		User user = userService.getUser();
		Recipe recipe = recipeRepository.findRecipeByIdAndUser(id, user);
		if (recipe == null) {
			throw new SecurityException();
		}

		// Check if it is already not active
		if (!recipe.isActive()) {
			throw new InvalidRequestException("The recipe is already not active");
		}

		// Add log entry
		RecipeLog recipeLog = new RecipeLog(recipe, RecipeLogEvent.INACTIVE);
		recipeLogRepository.save(recipeLog);

		// Turn off
		recipe.setActive(false);
		recipeRepository.save(recipe);
	}

	@Override
	public void publish(Long id) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public List<RecipeLog> getRecipeLogs(Long id, Integer page) {
		User user = userService.getUser();
		Recipe recipe = recipeRepository.findRecipeByIdAndUser(id, user);
		if (recipe == null) {
			throw new SecurityException();
		}
		if (page < 1) {
			page = 1;
		}
		Pageable pageable = new PageRequest(page - 1, 25);
		return recipeLogRepository.getRecipeLogByRecipeOrderByTimestampDesc(recipe, pageable);
	}

}
