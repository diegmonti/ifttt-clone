package iftttclone.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.Action;
import iftttclone.entities.ActionField;
import iftttclone.entities.Channel;
import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeActionField;
import iftttclone.entities.RecipeLog;
import iftttclone.entities.RecipeTriggerField;
import iftttclone.entities.Trigger;
import iftttclone.entities.TriggerField;
import iftttclone.entities.User;
import iftttclone.exceptions.InvalidRequestException;
import iftttclone.repositories.ActionRepository;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.RecipeLogRepository;
import iftttclone.repositories.RecipeRepository;
import iftttclone.repositories.TriggerRepository;
import iftttclone.services.interfaces.RecipeService;
import iftttclone.services.interfaces.UserService;
import iftttclone.utils.RecipeLogEvent;

@Component
@Transactional
public class RecipeServiceImpl implements RecipeService {
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private RecipeLogRepository recipeLogRepository;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private ChannelConnectorRepository channelConnectorRepository;
	@Autowired
	private TriggerRepository triggerRepository;
	@Autowired
	private ActionRepository actionRepository;
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
		// Fields are not null
		if (recipe.getTitle() == null || recipe.getTriggerChannelId() == null || recipe.getTriggerMethod() == null
				|| recipe.getRecipeTriggerFields() == null || recipe.getActionChannelId() == null
				|| recipe.getActionMethod() == null || recipe.getRecipeActionFields() == null) {
			throw new InvalidRequestException("A required field is missing");
		}

		// Title is not empty
		if (recipe.getTitle().equals("")) {
			throw new InvalidRequestException("The title cannot be empty");
		}

		// Check trigger channel
		Channel triggerChannel = channelRepository.findOne(recipe.getTriggerChannelId());
		if (triggerChannel == null) {
			throw new InvalidRequestException("The trigger channel is not valid");
		}

		// Check trigger channel connection
		if (triggerChannel.isWithConnection()) {
			if (channelConnectorRepository.getChannelConnectorByChannelAndUser(triggerChannel,
					userService.getUser()) == null) {
				throw new InvalidRequestException("The trigger channel must be connected");
			}
		}

		// Check trigger
		Trigger trigger = triggerRepository.getTriggerByMethodAndChannel(recipe.getTriggerMethod(), triggerChannel);
		if (trigger == null) {
			throw new InvalidRequestException("The trigger method is not valid");
		}
		recipe.setTrigger(trigger);

		// Check trigger fields presence
		for (TriggerField triggerField : trigger.getTriggerFields().values()) {
			if (!recipe.getRecipeTriggerFields().containsKey(triggerField.getParameter())) {
				throw new InvalidRequestException(
						"The trigger field " + triggerField.getParameter() + " is not present");
			}
			RecipeTriggerField recipeTriggerField = recipe.getRecipeTriggerFields().get(triggerField.getParameter());
			recipeTriggerField.setParameter(triggerField.getParameter());
			recipeTriggerField.setRecipe(recipe);
		}

		// Check trigger fields number
		if (trigger.getTriggerFields().values().size() != recipe.getRecipeTriggerFields().values().size()) {
			throw new InvalidRequestException("Too many trigger fields");
		}

		// Check trigger fields value
		for (RecipeTriggerField recipeTriggerField : recipe.getRecipeTriggerFields().values()) {
			if (recipeTriggerField.getValue() == null) {
				throw new InvalidRequestException("The trigger field cannot be empty");
			}
		}

		// Check action channel
		Channel actionChannel = channelRepository.findOne(recipe.getActionChannelId());
		if (actionChannel == null) {
			throw new InvalidRequestException("The action channel is not valid");
		}

		// Check action channel connection
		if (actionChannel.isWithConnection()) {
			if (channelConnectorRepository.getChannelConnectorByChannelAndUser(actionChannel,
					userService.getUser()) == null) {
				throw new InvalidRequestException("The action channel must be connected");
			}
		}

		// Check action
		Action action = actionRepository.getActionByMethodAndChannel(recipe.getActionMethod(), actionChannel);
		if (action == null) {
			throw new InvalidRequestException("The trigger method is not valid");
		}
		recipe.setAction(action);

		// Check action fields presence
		for (ActionField actionField : action.getActionFields().values()) {
			if (!recipe.getRecipeActionFields().containsKey(actionField.getParameter())) {
				throw new InvalidRequestException("The action field " + actionField.getParameter() + " is not present");
			}
			RecipeActionField recipeActionField = recipe.getRecipeActionFields().get(actionField.getParameter());
			recipeActionField.setParameter(actionField.getParameter());
			recipeActionField.setRecipe(recipe);
		}

		// Check action fields number
		if (action.getActionFields().values().size() != recipe.getRecipeActionFields().values().size()) {
			throw new InvalidRequestException("Too many action fields");
		}

		// Check action fields value
		for (RecipeActionField recipeActionField : recipe.getRecipeActionFields().values()) {
			if (recipeActionField.getValue() == null) {
				throw new InvalidRequestException("The action field cannot be empty");
			}
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
			recipeTriggerField.setParameter(triggerField.getParameter());
			recipeTriggerField.setRecipe(recipe);
		}

		// Check trigger fields number
		if (trigger.getTriggerFields().values().size() != stub.getRecipeTriggerFields().values().size()) {
			throw new InvalidRequestException("Too many trigger fields");
		}

		// Check trigger fields value
		for (RecipeTriggerField recipeTriggerField : stub.getRecipeTriggerFields().values()) {
			if (recipeTriggerField.getValue() == null) {
				throw new InvalidRequestException("The trigger field cannot be empty");
			}
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
			recipeActionField.setParameter(actionField.getParameter());
			recipeActionField.setRecipe(recipe);
		}

		// Check action fields number
		if (action.getActionFields().values().size() != stub.getRecipeActionFields().values().size()) {
			throw new InvalidRequestException("Too many action fields");
		}

		// Check action fields value
		for (RecipeActionField recipeActionField : stub.getRecipeActionFields().values()) {
			if (recipeActionField.getValue() == null) {
				throw new InvalidRequestException("The action field cannot be empty");
			}
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
		Pageable pageable = new PageRequest(page, 25);
		return recipeLogRepository.getRecipeLogByRecipeOrderByTimestampDesc(recipe, pageable);
	}

}
