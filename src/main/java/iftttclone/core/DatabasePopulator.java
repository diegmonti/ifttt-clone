package iftttclone.core;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.channels.annotations.ActionFieldTag;
import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.IngredientsTag;
import iftttclone.channels.annotations.TriggerFieldTag;
import iftttclone.channels.annotations.TriggerTag;
import iftttclone.entities.Action;
import iftttclone.entities.ActionField;
import iftttclone.entities.Channel;
import iftttclone.entities.Ingredient;
import iftttclone.entities.Trigger;
import iftttclone.entities.TriggerField;
import iftttclone.repositories.ActionFieldRepository;
import iftttclone.repositories.ActionRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.IngredientRepository;
import iftttclone.repositories.TriggerFieldRepository;
import iftttclone.repositories.TriggerRepository;

/**
 * This class is responsible for the initial insertion in the database of the
 * information related to available channels, triggers and actions. This
 * information are obtained from the annotations of the classes that implement a
 * channel. If the field of an annotation is changed, the database is updated
 * the next time the application is deployed.
 */
@Component
public class DatabasePopulator {
	@Autowired
	private ChannelRepository channels;
	@Autowired
	private TriggerRepository triggers;
	@Autowired
	private TriggerFieldRepository triggerFields;
	@Autowired
	private IngredientRepository ingredients;
	@Autowired
	private ActionRepository actions;
	@Autowired
	private ActionFieldRepository actionFields;

	@PostConstruct
	@Transactional
	private void populateDatabase() {
		System.err.println("POPULATOR: Start database population");

		Reflections reflections = new Reflections("iftttclone.channels");
		Set<Class<?>> channelClasses = reflections.getTypesAnnotatedWith(ChannelTag.class);

		for (final Class<?> channelClass : channelClasses) {
			System.err.println("POPULATOR: Processing class " + channelClass.getName());

			Channel oldChannel = channels.getChannelByClasspath(channelClass.getName());
			if (oldChannel == null)
				populateChannel(channelClass, new Channel());
			else
				populateChannel(channelClass, oldChannel);
		}

		System.err.println("POPULATOR: End database population");
	}

	private void populateChannel(Class<?> channelClass, Channel channel) {
		channel.setClasspath(channelClass.getName());
		ChannelTag channelTag = channelClass.getAnnotation(ChannelTag.class);
		channel.setId(channelTag.name().toLowerCase().replace(" ", "_"));
		channel.setName(channelTag.name());
		channel.setDescription(channelTag.description());
		channel.setWithConnection(channelTag.withConnection());

		channels.save(channel);

		// For each method
		for (Method method : channelClass.getMethods()) {
			if (method.isAnnotationPresent(TriggerTag.class)) {
				Trigger oldTrigger = triggers.getTriggerByMethodAndChannel(method.getName(), channel);
				if (oldTrigger == null)
					populateTrigger(method, channel, new Trigger());
				else
					populateTrigger(method, channel, oldTrigger);
			} else if (method.isAnnotationPresent(ActionTag.class)) {
				Action oldAction = actions.getActionByMethodAndChannel(method.getName(), channel);
				if (oldAction == null)
					populateAction(method, channel, new Action());
				else
					populateAction(method, channel, oldAction);
			}
		}

	}

	private void populateTrigger(Method method, Channel channel, Trigger trigger) {
		trigger.setChannel(channel);
		trigger.setMethod(method.getName());
		TriggerTag triggerTag = method.getAnnotation(TriggerTag.class);
		trigger.setName(triggerTag.name());
		trigger.setDescription(triggerTag.description());

		triggers.save(trigger);

		// For each parameter
		for (Parameter parameter : method.getParameters()) {
			if (parameter.isAnnotationPresent(TriggerFieldTag.class)) {
				TriggerField triggerField = triggerFields.getTriggerFieldByParameterAndTrigger(parameter.getName(),
						trigger);
				if (triggerField == null)
					triggerField = new TriggerField();

				triggerField.setTrigger(trigger);
				triggerField.setParameter(parameter.getName());
				TriggerFieldTag triggerFieldTag = parameter.getAnnotation(TriggerFieldTag.class);
				triggerField.setName(triggerFieldTag.name());
				triggerField.setDescription(triggerFieldTag.description());

				triggerFields.save(triggerField);
			}
		}

		// For each ingredient
		if (method.isAnnotationPresent(IngredientsTag.class)) {
			for (IngredientTag ingredientTag : method.getAnnotation(IngredientsTag.class).value()) {
				Ingredient ingredient = ingredients.getIngredientByNameAndTrigger(ingredientTag.name(), trigger);
				if (ingredient == null)
					ingredient = new Ingredient();

				ingredient.setTrigger(trigger);
				ingredient.setName(ingredientTag.name());
				ingredient.setDescription(ingredientTag.description());
				ingredient.setExample(ingredientTag.example());

				ingredients.save(ingredient);
			}
		}

	}

	private void populateAction(Method method, Channel channel, Action action) {
		action.setChannel(channel);
		action.setMethod(method.getName());
		ActionTag actionTag = method.getAnnotation(ActionTag.class);
		action.setName(actionTag.name());
		action.setDescription(actionTag.description());

		actions.save(action);

		// For each parameter
		for (Parameter parameter : method.getParameters()) {
			if (parameter.isAnnotationPresent(ActionFieldTag.class)) {
				ActionField actionField = actionFields.getActionFieldByParameterAndAction(parameter.getName(), action);
				if (actionField == null)
					actionField = new ActionField();

				actionField.setAction(action);
				actionField.setParameter(parameter.getName());
				ActionFieldTag actionFieldTag = parameter.getAnnotation(ActionFieldTag.class);
				actionField.setName(actionFieldTag.name());
				actionField.setDescription(actionFieldTag.description());

				actionFields.save(actionField);
			}
		}
	}
}
