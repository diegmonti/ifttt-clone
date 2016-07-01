package iftttclone.core;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
//import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.channels.AbstractChannel;
import iftttclone.entities.Action;
import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeActionField;
import iftttclone.entities.RecipeLog;
import iftttclone.entities.RecipeLogEvent;
import iftttclone.entities.RecipeTriggerField;
import iftttclone.entities.Trigger;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.RecipeLogRepository;
import iftttclone.repositories.RecipeRepository;

/**
 * This class is responsible for processing the active recipes.
 */
@Component
public class Scheduler {
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private RecipeLogRepository recipeLogRepository;
	@Autowired
	private ChannelConnectorRepository channelConnectorRepository;

	/**
	 * This method executes the trigger of active recipes every 15 minutes. If
	 * the trigger returns null, the action is not run. Otherwise each field of
	 * the action is parsed, substituting every {{ingredient}} with the value
	 * returned by the trigger, then the action is called.
	 */
	@Transactional//(propagation = Propagation.REQUIRES_NEW)
	@Scheduled(fixedDelayString = "${scheduler.fixedDelay}")
	public void run() {
		System.err.println("----SCHEDULER: Start processing recipes");
		Iterator<Recipe> recipes = recipeRepository.findAll().iterator();
		Recipe recipe;

		while (recipes.hasNext()) {
			recipe = recipes.next();
			try {
				List<Map<String, String>> triggerResult;

				// Check if the recipe is active
				if (recipe.isActive()) {
					System.err.println("----SCHEDULER: Processing recipe " + recipe.getTitle());
					triggerResult = runTrigger(recipe);

					// Run the action?
					if ((triggerResult != null) && (triggerResult.size() > 0)) {
						System.err.println("----SCHEDULER: Running action for recipe " + recipe.getTitle());
						runAction(recipe, triggerResult);
						recipe.setLastRun(System.currentTimeMillis());
						recipe.setRuns(recipe.getRuns() + 1);
						recipeLogRepository.save(new RecipeLog(recipe, RecipeLogEvent.RUN));
					} else {
						recipeLogRepository.save(new RecipeLog(recipe, RecipeLogEvent.CHECK));
					}

					recipeRepository.save(recipe);
				}

			} catch (ReflectiveOperationException e) {
				System.err.println("----SCHEDULER: Scheduler exception");
				// e.printStackTrace();
				recipeLogRepository.save(new RecipeLog(recipe, RecipeLogEvent.ERROR));
			}
		}
		System.err.println("----SCHEDULER: End processing recipes");
	}

	@SuppressWarnings("unchecked")
	private List<Map<String, String>> runTrigger(Recipe recipe) throws ReflectiveOperationException {
		Trigger trigger = recipe.getTrigger();

		Class<AbstractChannel> channelClass = (Class<AbstractChannel>) Class
				.forName(trigger.getChannel().getClasspath());

		// Create a new instance of the channel
		AbstractChannel instance = channelClass.newInstance();
		instance.setChannelConnector(
				channelConnectorRepository.getChannelConnectorByChannelAndUser(trigger.getChannel(), recipe.getUser()));
		instance.setLastRun(new Date(recipe.getLastRun()));
		instance.setUser(recipe.getUser());
		/*instance.setRecipeId(recipe.getId());
		instance.setRecipeRepository(recipeRepository);*/

		// Load parameters
		Map<String, RecipeTriggerField> recipeTriggerFields = recipe.getRecipeTriggerFields();
		Integer i = 0;
		List<String> parameters = new ArrayList<String>();
		while (recipeTriggerFields.containsKey("arg" + i)) {
			parameters.add(recipeTriggerFields.get("arg" + i).getValue());
			i++;
		}

		// All parameters are strings
		Class<?>[] parameterTypes = new Class<?>[i];
		for (Integer j = 0; j < i; j++) {
			parameterTypes[j] = String.class;
		}

		Method method = channelClass.getMethod(trigger.getMethod(), parameterTypes);
		return (List<Map<String, String>>) method.invoke(instance, parameters.toArray());
	}

	@SuppressWarnings("unchecked")
	private void runAction(Recipe recipe, List<Map<String, String>> triggerResult) throws ReflectiveOperationException {
		Action action = recipe.getAction();

		Class<AbstractChannel> channelClass = (Class<AbstractChannel>) Class
				.forName(action.getChannel().getClasspath());

		// Create a new instance of the channel
		AbstractChannel instance = channelClass.newInstance();
		instance.setChannelConnector(
				channelConnectorRepository.getChannelConnectorByChannelAndUser(action.getChannel(), recipe.getUser()));
		instance.setLastRun(new Date(recipe.getLastRun()));
		instance.setUser(recipe.getUser());
		/*instance.setRecipeId(recipe.getId());	// not needed as of now
		instance.setRecipeRepository(recipeRepository);	// not needed as of now*/

		// Load parameters
		Map<String, RecipeActionField> recipeActionFields = recipe.getRecipeActionFields();
		// All parameters are strings
		Class<?>[] parameterTypes = new Class<?>[recipeActionFields.keySet().size()];
		// Assuming that everything is fine (as it should)
		for (Integer j = 0; j < recipeActionFields.keySet().size(); j++) {
			parameterTypes[j] = String.class;
		}
		Method method = channelClass.getMethod(action.getMethod(), parameterTypes);
		// Run action
		for (Map<String, String> triggerResultEntry : triggerResult) {
			Integer i = 0;
			List<String> parameters = new ArrayList<String>();
			while (recipeActionFields.containsKey("arg" + i)) {
				parameters.add(parseField(recipeActionFields.get("arg" + i).getValue(), triggerResultEntry));
				i++;
			}

			method.invoke(instance, parameters.toArray());
		}
	}

	private String parseField(String input, Map<String, String> map) {
		String output = input;
		for (String key : map.keySet()) {
			output = output.replace("{{" + key + "}}", map.get(key));
		}
		return output;
	}
}
