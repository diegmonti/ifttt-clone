package iftttclone.services;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.core.Validator;
import iftttclone.entities.Action;
import iftttclone.entities.ActionField;
import iftttclone.entities.Channel;
import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeActionField;
import iftttclone.entities.RecipeLog;
import iftttclone.entities.RecipeLogEvent;
import iftttclone.entities.RecipeTriggerField;
import iftttclone.entities.Trigger;
import iftttclone.entities.TriggerField;
import iftttclone.entities.User;
import iftttclone.exceptions.InvalidRequestException;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.RecipeLogRepository;
import iftttclone.repositories.RecipeRepository;
import iftttclone.services.interfaces.RecipeService;
import iftttclone.services.interfaces.UserService;

@Component
@Transactional
public class RecipeServiceImpl implements RecipeService {
	private static final Integer PAGE_SIZE = 25;

	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private RecipeLogRepository recipeLogRepository;
	@Autowired
	private ChannelConnectorRepository channelConnectorRepository;
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
		return recipe;
	}

	@Override
	public Recipe addRecipe(Recipe recipe) {
		// Fields are not null
		if (recipe.getTitle() == null || recipe.getTrigger() == null || recipe.getRecipeTriggerFields() == null
				|| recipe.getAction() == null || recipe.getRecipeActionFields() == null) {
			throw new InvalidRequestException("A required field is missing or inconsistent");
		}

		// Title is not empty
		if (recipe.getTitle().equals("")) {
			throw new InvalidRequestException("The title cannot be empty");
		}
		
		Channel triggerChannel = recipe.getTrigger().getChannel();

		// Check trigger channel connection
		if (triggerChannel.isWithConnection()) {
			if (channelConnectorRepository.getChannelConnectorByChannelAndUser(triggerChannel,
					userService.getUser()) == null) {
				throw new InvalidRequestException("The trigger channel must be connected");
			}
		}
		
		Collection<TriggerField> triggerFields = recipe.getTrigger().getTriggerFields().values();

		// Check trigger fields presence
		for (TriggerField triggerField : triggerFields) {
			if (!recipe.getRecipeTriggerFields().containsKey(triggerField.getParameter())) {
				throw new InvalidRequestException(
						"The trigger field " + triggerField.getParameter() + " is not present");
			}
			RecipeTriggerField recipeTriggerField = recipe.getRecipeTriggerFields().get(triggerField.getParameter());
			Validator.validate(recipeTriggerField.getValue(), triggerField.getType(), triggerField.getName());
			recipeTriggerField.setParameter(triggerField.getParameter());
			recipeTriggerField.setRecipe(recipe);
		}

		// Check trigger fields number
		if (triggerFields.size() != recipe.getRecipeTriggerFields().values().size()) {
			throw new InvalidRequestException("Too many trigger fields");
		}

		Channel actionChannel = recipe.getAction().getChannel();

		// Check action channel connection
		if (actionChannel.isWithConnection()) {
			if (channelConnectorRepository.getChannelConnectorByChannelAndUser(actionChannel,
					userService.getUser()) == null) {
				throw new InvalidRequestException("The action channel must be connected");
			}
		}

		Collection<ActionField> actionsFields = recipe.getAction().getActionFields().values();

		// Check action fields presence
		for (ActionField actionField : actionsFields) {
			if (!recipe.getRecipeActionFields().containsKey(actionField.getParameter())) {
				throw new InvalidRequestException("The action field " + actionField.getParameter() + " is not present");
			}
			RecipeActionField recipeActionField = recipe.getRecipeActionFields().get(actionField.getParameter());
			Validator.validate(recipeActionField.getValue(), actionField.getType(), actionField.getName());
			recipeActionField.setParameter(actionField.getParameter());
			recipeActionField.setRecipe(recipe);
		}

		// Check action fields number
		if (actionsFields.size() != recipe.getRecipeActionFields().values().size()) {
			throw new InvalidRequestException("Too many action fields");
		}

		// Set default values
		recipe.setCreationTime(System.currentTimeMillis());
		recipe.setLastRun(System.currentTimeMillis());
		recipe.setUser(userService.getUser());
		recipe.setActive(true);
		recipe.setRuns(0);

		recipeRepository.save(recipe);

		// Add log entry
		RecipeLog recipeLog = new RecipeLog(recipe, RecipeLogEvent.NEW);
		recipeLogRepository.save(recipeLog);

		return recipe;
	}

	@Override
	public Recipe updateRecipe(Long id, Recipe stub) {
		Recipe recipe = recipeRepository.findRecipeByIdAndUser(id, userService.getUser());
		if (recipe == null) {
			new SecurityException();
		}

		// Fields are not null
		if (stub.getTitle() == null || stub.getRecipeTriggerFields() == null || stub.getRecipeActionFields() == null) {
			throw new InvalidRequestException("A required field is missing");
		}

		// Title is not empty
		if (stub.getTitle().equals("")) {
			throw new InvalidRequestException("The title cannot be empty");
		}
		recipe.setTitle(stub.getTitle());

		// Check trigger
		Trigger trigger = recipe.getTrigger();

		// Check trigger fields presence
		for (TriggerField triggerField : trigger.getTriggerFields().values()) {
			if (!stub.getRecipeTriggerFields().containsKey(triggerField.getParameter())) {
				throw new InvalidRequestException(
						"The trigger field " + triggerField.getParameter() + " is not present");
			}
			RecipeTriggerField recipeTriggerField = stub.getRecipeTriggerFields().get(triggerField.getParameter());
			Validator.validate(recipeTriggerField.getValue(), triggerField.getType(), triggerField.getName());
			recipeTriggerField.setParameter(triggerField.getParameter());
			recipeTriggerField.setRecipe(recipe);
		}

		// Check trigger fields number
		if (trigger.getTriggerFields().values().size() != stub.getRecipeTriggerFields().values().size()) {
			throw new InvalidRequestException("Too many trigger fields");
		}

		recipe.setRecipeTriggerFields(stub.getRecipeTriggerFields());

		// Check action
		Action action = recipe.getAction();

		// Check action fields presence
		for (ActionField actionField : action.getActionFields().values()) {
			if (!stub.getRecipeActionFields().containsKey(actionField.getParameter())) {
				throw new InvalidRequestException("The action field " + actionField.getParameter() + " is not present");
			}
			RecipeActionField recipeActionField = stub.getRecipeActionFields().get(actionField.getParameter());
			Validator.validate(recipeActionField.getValue(), actionField.getType(), actionField.getName());
			recipeActionField.setParameter(actionField.getParameter());
			recipeActionField.setRecipe(recipe);
		}

		// Check action fields number
		if (action.getActionFields().values().size() != stub.getRecipeActionFields().values().size()) {
			throw new InvalidRequestException("Too many action fields");
		}

		recipe.setRecipeActionFields(stub.getRecipeActionFields());

		recipe.setLastRun(System.currentTimeMillis());
		recipeRepository.save(recipe);

		// Add log entry
		RecipeLog recipeLog = new RecipeLog(recipe, RecipeLogEvent.UPDATE);
		recipeLogRepository.save(recipeLog);

		return recipe;
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
		Recipe recipe = recipeRepository.findRecipeByIdAndUser(id, userService.getUser());
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
		Recipe recipe = recipeRepository.findRecipeByIdAndUser(id, userService.getUser());
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
	public List<RecipeLog> getRecipeLogs(Long id, Integer page) {
		Recipe recipe = recipeRepository.findRecipeByIdAndUser(id, userService.getUser());
		if (recipe == null) {
			throw new SecurityException();
		}
		if (page < 0) {
			throw new InvalidRequestException("Page cannot be negative");
		}
		Pageable pageable = new PageRequest(page, PAGE_SIZE);
		return recipeLogRepository.getRecipeLogByRecipeOrderByTimestampDesc(recipe, pageable);
	}

}
