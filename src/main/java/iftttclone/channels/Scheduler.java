package iftttclone.channels;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Set;

import javax.annotation.PostConstruct;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.reflections.Reflections;
import org.springframework.beans.factory.annotation.Autowired;

import iftttclone.channels.annotation.ActionFieldTag;
import iftttclone.channels.annotation.ActionTag;
import iftttclone.channels.annotation.ChannelTag;
import iftttclone.channels.annotation.IngredientTag;
import iftttclone.channels.annotation.IngredientsTag;
import iftttclone.channels.annotation.TriggerFieldTag;
import iftttclone.channels.annotation.TriggerTag;
import iftttclone.entities.Action;
import iftttclone.entities.ActionField;
import iftttclone.entities.Channel;
import iftttclone.entities.Ingredient;
import iftttclone.entities.Trigger;
import iftttclone.entities.TriggerField;

public class Scheduler {
	// Quando inizializzato lo scheduler analizza le annotazioni e popola o aggiorna il database
	// Lo scheduler, ogni X minuti, cerca le ricette attive ed esegue il trigger (con la reflection)
	// Se non ritorna null prende il testo dei field dell'action, lo processa sostituendo
	// tutti gli {{ingrendient}} con il valore che recupera dalla mappa e poi chiama la relativa action
	// Le classi dei canali devono in qualche modo sapere l'utente corrente e il relativo token
	@Autowired
	SessionFactory sessionFactory;
	
	// TODO bisogna verificare se sono già presenti nel database e, nel caso, aggiornare e non mettere un duplicato
	@PostConstruct
	public void populateDatabase() {
		System.err.println("SCHEDULER: start database population");
		Reflections reflections = new Reflections(this.getClass().getPackage());
		Set<Class<?>> channelClasses = reflections.getTypesAnnotatedWith(ChannelTag.class);
		for (final Class<?> channelClass : channelClasses) {
			System.err.println("SCHEDULER: processing class " + channelClass.getName());
			addChannel(channelClass);
		}
	}
	
	private void addChannel(Class<?> channelClass) {
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		// Create a new channel
		Channel channel = new Channel();
		channel.setClasspath(channelClass.getName());
		ChannelTag channelTag = channelClass.getAnnotation(ChannelTag.class);
		channel.setName(channelTag.name());
		channel.setDescription(channelTag.description());
		session.save(channel);
		
		// For each method
		for (Method method : channelClass.getMethods()) {
			if (method.isAnnotationPresent(TriggerTag.class)) {
				createTrigger(method, channel, session);
			} else if (method.isAnnotationPresent(ActionTag.class)) {
				createAction(method, channel, session);
			}
		}
		
		transaction.commit();
	}
	
	private void createTrigger(Method method, Channel channel, Session session) {
		Trigger trigger = new Trigger();
		trigger.setChannel(channel);
		trigger.setMethod(method.getName());
		TriggerTag triggerTag = method.getAnnotation(TriggerTag.class);
		trigger.setName(triggerTag.name());
		trigger.setDescription(triggerTag.description());
		session.save(trigger);

		// For each parameter
		for (Parameter parameter : method.getParameters()) {
			if (parameter.isAnnotationPresent(TriggerFieldTag.class)) {
				TriggerField triggerField = new TriggerField();
				triggerField.setTrigger(trigger);
				triggerField.setParameter(parameter.getName());
				TriggerFieldTag triggerFieldTag = parameter.getAnnotation(TriggerFieldTag.class);
				triggerField.setName(triggerFieldTag.name());
				triggerField.setDescription(triggerFieldTag.description());
				session.save(triggerField);
			}
		}
		
		// For each ingredient
		if (method.isAnnotationPresent(IngredientsTag.class)) {
			for (IngredientTag ingredientTag : method.getAnnotation(IngredientsTag.class).value()) {
				Ingredient ingredient = new Ingredient();
				ingredient.setTrigger(trigger);
				ingredient.setName(ingredientTag.name());
				ingredient.setDescription(ingredientTag.description());
				ingredient.setExample(ingredientTag.example());
				session.save(ingredient);
			}
		}
	}
	
	private void createAction(Method method, Channel channel, Session session) {
		Action action = new Action();
		action.setChannel(channel);
		action.setMethod(method.getName());
		ActionTag actionTag = method.getAnnotation(ActionTag.class);
		action.setName(actionTag.name());
		action.setDescription(actionTag.description());
		session.save(action);
		
		// For each parameter
		for (Parameter parameter : method.getParameters()) {
			if (parameter.isAnnotationPresent(ActionFieldTag.class)) {
				ActionField actionField = new ActionField();
				actionField.setAction(action);
				actionField.setParameter(parameter.getName());
				ActionFieldTag actionFieldTag = parameter.getAnnotation(ActionFieldTag.class);
				actionField.setName(actionFieldTag.name());
				actionField.setDescription(actionFieldTag.description());
				session.save(actionField);
			}
		}
	}
}
