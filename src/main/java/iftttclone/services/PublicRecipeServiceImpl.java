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
import iftttclone.entities.PublicRecipe;
import iftttclone.entities.PublicRecipeActionField;
import iftttclone.entities.PublicRecipeTriggerField;
import iftttclone.entities.Trigger;
import iftttclone.entities.TriggerField;
import iftttclone.entities.User;
import iftttclone.exceptions.InvalidRequestException;
import iftttclone.exceptions.ResourceNotFoundException;
import iftttclone.repositories.PublicRecipeRepository;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.PublicRecipeService;
import iftttclone.services.interfaces.UserService;

@Component
@Transactional
public class PublicRecipeServiceImpl implements PublicRecipeService {
	private static final Integer PAGE_SIZE = 25;

	@Autowired
	private PublicRecipeRepository publicRecipeRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;

	@Override
	public List<PublicRecipe> getPublicRecipes(Integer page) {
		if (page < 0) {
			throw new InvalidRequestException("Page cannot be negative");
		}
		Pageable pageable = new PageRequest(page, PAGE_SIZE);
		return publicRecipeRepository.findAll(pageable);
	}

	@Override
	public List<PublicRecipe> getPublicRecipesByTitle(String title, Integer page) {
		if (page < 0) {
			throw new InvalidRequestException("Page cannot be negative");
		}
		Pageable pageable = new PageRequest(page, PAGE_SIZE);
		return publicRecipeRepository.findAllByTitleContaining(title, pageable);
	}

	@Override
	public PublicRecipe getPublicRecipe(Long publicRecipeId) {
		PublicRecipe publicRecipe = publicRecipeRepository.findOne(publicRecipeId);
		if (publicRecipe == null) {
			throw new ResourceNotFoundException();
		}
		return publicRecipe;
	}

	@Override
	public PublicRecipe addPublicRecipe(PublicRecipe publicRecipe) {
		// Fields are not null
		if (publicRecipe.getTitle() == null || publicRecipe.getDescription() == null
				|| publicRecipe.getTrigger() == null || publicRecipe.getPublicRecipeTriggerFields() == null
				|| publicRecipe.getAction() == null || publicRecipe.getPublicRecipeActionFields() == null) {
			throw new InvalidRequestException("A required field is missing or inconsistent");
		}

		// Title is not empty
		if (publicRecipe.getTitle().equals("")) {
			throw new InvalidRequestException("The title cannot be empty");
		}

		// Description is not empty
		if (publicRecipe.getDescription().equals("")) {
			throw new InvalidRequestException("The description cannot be empty");
		}

		Collection<TriggerField> triggerFields = publicRecipe.getTrigger().getTriggerFields().values();
		Integer validTriggerFields = 0;

		// Set trigger field parameter and recipe
		for (TriggerField triggerField : triggerFields) {
			if (publicRecipe.getPublicRecipeTriggerFields().containsKey(triggerField.getParameter())) {
				PublicRecipeTriggerField recipeTriggerField = publicRecipe.getPublicRecipeTriggerFields()
						.get(triggerField.getParameter());
				Validator.validate(recipeTriggerField.getValue(), triggerField.getType(), triggerField.getName());
				recipeTriggerField.setParameter(triggerField.getParameter());
				recipeTriggerField.setPublicRecipe(publicRecipe);
				validTriggerFields++;
			}
		}

		// Check trigger fields number
		if (validTriggerFields != publicRecipe.getPublicRecipeTriggerFields().values().size()) {
			throw new InvalidRequestException("The name of a trigger field is not correct");
		}

		Collection<ActionField> actionsFields = publicRecipe.getAction().getActionFields().values();
		Integer validActionFields = 0;

		// Set action field parameter and recipe
		for (ActionField actionField : actionsFields) {
			if (publicRecipe.getPublicRecipeActionFields().containsKey(actionField.getParameter())) {
				PublicRecipeActionField recipeActionField = publicRecipe.getPublicRecipeActionFields()
						.get(actionField.getParameter());
				Validator.validate(recipeActionField.getValue(), actionField.getType(), actionField.getName());
				recipeActionField.setParameter(actionField.getParameter());
				recipeActionField.setPublicRecipe(publicRecipe);
				validActionFields++;
			}
		}

		// Check action fields number
		if (validActionFields != publicRecipe.getPublicRecipeActionFields().values().size()) {
			throw new InvalidRequestException("The name of an action field is not correct");
		}

		// Set default values
		publicRecipe.setUser(userService.getUser());
		publicRecipe.setFavorites(0);

		publicRecipeRepository.save(publicRecipe);

		return publicRecipe;
	}

	@Override
	public PublicRecipe updatePublicRecipe(Long publicRecipeId, PublicRecipe stub) {
		PublicRecipe publicRecipe = publicRecipeRepository.findPublicRecipeByIdAndUser(publicRecipeId,
				userService.getUser());
		if (publicRecipe == null) {
			new SecurityException();
		}

		// Fields are not null
		if (stub.getTitle() == null || stub.getDescription() == null || stub.getPublicRecipeTriggerFields() == null
				|| stub.getPublicRecipeActionFields() == null) {
			throw new InvalidRequestException("A required field is missing");
		}

		// Title is not empty
		if (stub.getTitle().equals("")) {
			throw new InvalidRequestException("The title cannot be empty");
		}
		publicRecipe.setTitle(stub.getTitle());

		// Description is not empty
		if (stub.getDescription().equals("")) {
			throw new InvalidRequestException("The description cannot be empty");
		}
		publicRecipe.setDescription(stub.getDescription());

		// Check trigger
		Trigger trigger = publicRecipe.getTrigger();
		Integer validTriggerFields = 0;

		// Set trigger field parameter and recipe
		for (TriggerField triggerField : trigger.getTriggerFields().values()) {
			if (stub.getPublicRecipeTriggerFields().containsKey(triggerField.getParameter())) {
				PublicRecipeTriggerField recipeTriggerField = stub.getPublicRecipeTriggerFields()
						.get(triggerField.getParameter());
				Validator.validate(recipeTriggerField.getValue(), triggerField.getType(), triggerField.getName());
				recipeTriggerField.setParameter(triggerField.getParameter());
				recipeTriggerField.setPublicRecipe(publicRecipe);
				validTriggerFields++;
			}
		}

		// Check trigger fields number
		if (validTriggerFields != stub.getPublicRecipeTriggerFields().values().size()) {
			throw new InvalidRequestException("The name of a trigger field is not correct");
		}

		publicRecipe.setPublicRecipeTriggerFields(stub.getPublicRecipeTriggerFields());

		// Check action
		Action action = publicRecipe.getAction();
		Integer validActionFields = 0;

		// Set action field parameter and recipe
		for (ActionField actionField : action.getActionFields().values()) {
			if (stub.getPublicRecipeActionFields().containsKey(actionField.getParameter())) {
				PublicRecipeActionField recipeActionField = stub.getPublicRecipeActionFields()
						.get(actionField.getParameter());
				Validator.validate(recipeActionField.getValue(), actionField.getType(), actionField.getName());
				recipeActionField.setParameter(actionField.getParameter());
				recipeActionField.setPublicRecipe(publicRecipe);
				validActionFields++;
			}
		}

		// Check action fields number
		if (validActionFields != stub.getPublicRecipeActionFields().values().size()) {
			throw new InvalidRequestException("The name of an action field is not correct");
		}

		publicRecipe.setPublicRecipeActionFields(stub.getPublicRecipeActionFields());

		publicRecipeRepository.save(publicRecipe);

		return publicRecipe;
	}

	@Override
	public void deletePublicRecipe(Long publicRecipeId) {
		User user = userService.getUser();
		PublicRecipe publicRecipe = publicRecipeRepository.findPublicRecipeByIdAndUser(publicRecipeId, user);
		if (publicRecipe == null) {
			throw new SecurityException();
		}
		publicRecipeRepository.delete(publicRecipe);
	}

	@Override
	public void addToFavorite(Long publicRecipeId) {
		User user = userService.getUser();
		PublicRecipe publicRecipe = publicRecipeRepository.findOne(publicRecipeId);
		if (publicRecipe == null) {
			throw new ResourceNotFoundException();
		}
		if (!user.getFavoritePublicRecipes().contains(publicRecipe)) {
			publicRecipe.setFavorites(publicRecipe.getFavorites() + 1);
			publicRecipeRepository.save(publicRecipe);
			user.getFavoritePublicRecipes().add(publicRecipe);
			userRepository.save(user);
		} else {
			throw new InvalidRequestException("The public recipe is already favorite");
		}
	}

	@Override
	public void removeFromFavorite(Long publicRecipeId) {
		User user = userService.getUser();
		PublicRecipe publicRecipe = publicRecipeRepository.findOne(publicRecipeId);
		if (publicRecipe == null) {
			throw new ResourceNotFoundException();
		}
		if (user.getFavoritePublicRecipes().contains(publicRecipe)) {
			publicRecipe.setFavorites(publicRecipe.getFavorites() - 1);
			publicRecipeRepository.save(publicRecipe);
			user.getFavoritePublicRecipes().remove(publicRecipe);
			userRepository.save(user);
		} else {
			throw new InvalidRequestException("The public recipe is already not favorite");
		}
	}

	@Override
	public Set<PublicRecipe> getFavoritePublicRecipes() {
		User user = userService.getUser();
		return user.getFavoritePublicRecipes();
	}

	@Override
	public List<PublicRecipe> getPublishedPublicRecipes(Integer page) {
		if (page < 0) {
			throw new InvalidRequestException("Page cannot be negative");
		}
		User user = userService.getUser();
		Pageable pageable = new PageRequest(page, PAGE_SIZE);
		return publicRecipeRepository.findAllByUser(user, pageable);
	}

}
