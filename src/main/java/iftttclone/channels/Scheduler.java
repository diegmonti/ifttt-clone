package iftttclone.channels;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.List;
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
	

	@PostConstruct
	public void populateDatabase() {
		System.err.println("SCHEDULER: start database population");
		
		Session session = sessionFactory.getCurrentSession();
		Transaction transaction = session.beginTransaction();
		
		Reflections reflections = new Reflections(this.getClass().getPackage());
		
		Set<Class<?>> channelClasses = reflections.getTypesAnnotatedWith(ChannelTag.class);
		
		
		@SuppressWarnings("unchecked")
		List<Channel> channels = (List<Channel>)session.createCriteria(Channel.class).list();
		
		for (final Class<?> channelClass : channelClasses) {
			System.err.println("SCHEDULER: processing class " + channelClass.getName());
			
			addChannel(channelClass, session, channels);
		}
		
		transaction.commit();
	}
	
	private void addChannel(Class<?> channelClass, Session session, List<Channel> presentChannels) {
		
		// Create a new channel
		Channel channel = new Channel();
		channel.setClasspath(channelClass.getName());
		ChannelTag channelTag = channelClass.getAnnotation(ChannelTag.class);
		channel.setName(channelTag.name());
		channel.setDescription(channelTag.description());
		
		// search the channel in the list
		boolean found = false;
		Collection<Trigger> triggers = null;
		Collection<Action> actions = null;
		for(int i = 0; i < presentChannels.size(); i++){
			if(presentChannels.get(i).getClasspath().equals(channel.getClasspath())){
				found = true;
				triggers = presentChannels.get(i).getTriggers();
				actions = presentChannels.get(i).getActions();
				channel.setId(presentChannels.get(i).getId());
				break;
			}
		}
		if(!found) session.save(channel);
		
		// For each method
		for (Method method : channelClass.getMethods()) {
			if (method.isAnnotationPresent(TriggerTag.class)) {
				createTrigger(method, channel, session, triggers);
			} else if (method.isAnnotationPresent(ActionTag.class)) {
				createAction(method, channel, session, actions);
			}
		}
		
		
	}
	
	private void createTrigger(Method method, Channel channel, Session session, Collection<Trigger> triggers) {
		Trigger trigger = new Trigger();
		trigger.setChannel(channel);
		trigger.setMethod(method.getName());
		TriggerTag triggerTag = method.getAnnotation(TriggerTag.class);
		trigger.setName(triggerTag.name());
		trigger.setDescription(triggerTag.description());
		
		
		Collection<TriggerField> fields = null;
		Collection<Ingredient> ingredients = null;
		if(triggers == null) {
			System.out.println("triggers are null for " + channel.getName());
			session.save(trigger);
		}
		else{
			// here i suppose that "Channel + triggerName" must be unique
			boolean triggerFound = false;
			
			for(Trigger t : triggers){
				if(channel.getId() == t.getChannel().getId() && t.getName().equals(trigger.getName())){
					triggerFound = true;
					fields = t.getTriggerFields();
					ingredients = t.getIngredients();
					trigger.setId(t.getId());
					break;
				}
			}
			if(!triggerFound) session.save(trigger);
		}
		
		// For each parameter
		for (Parameter parameter : method.getParameters()) {
			if (parameter.isAnnotationPresent(TriggerFieldTag.class)) {
				TriggerField triggerField = new TriggerField();
				triggerField.setTrigger(trigger);
				triggerField.setParameter(parameter.getName());
				TriggerFieldTag triggerFieldTag = parameter.getAnnotation(TriggerFieldTag.class);
				triggerField.setName(triggerFieldTag.name());
				triggerField.setDescription(triggerFieldTag.description());
				
				if(fields == null){ 
					session.save(triggerField);
					System.out.println("fields are null here for " + trigger.getChannel().getName() + " - " + trigger.getName());
				}
				else{
					// need to search if this field is there
					boolean fieldFound = false;
					for(TriggerField f : fields){
						if(f.getTrigger().getId() == triggerField.getTrigger().getId() && // if it belongs to the same trigge
								f.getName().equals(triggerField.getName())); // and has the same name
						// if is the same field
						fieldFound = true;
						break;
					}
					if(!fieldFound) session.save(triggerField);
				}
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
				
				if(ingredients == null) session.save(ingredient);
				else {
					// need to search if this field is there
					boolean ingredientFound = false;
					for(Ingredient i : ingredients){
						if(i.getTrigger().getId() == ingredient.getTrigger().getId() && // if it belongs to the same trigge
								i.getName().equals(ingredient.getName())); // and has the same name
						// if is the same ingredient
						ingredientFound = true;
						break;
					}
					if(!ingredientFound) session.save(ingredient);
				}
			}
		}
	}
	
	private void createAction(Method method, Channel channel, Session session, Collection<Action> actions) {
		Action action = new Action();
		action.setChannel(channel);
		action.setMethod(method.getName());
		ActionTag actionTag = method.getAnnotation(ActionTag.class);
		action.setName(actionTag.name());
		action.setDescription(actionTag.description());
		
		Collection<ActionField> actionFields= null;
		if (actions == null) session.save(action);
		else{
			boolean actionFound = false;
			for(Action a : actions){
				if(a.getChannel().getId() == action.getChannel().getId() && 
						a.getName().equals(action.getName())){
					actionFound = true;
					actionFields = a.getActionFields();
					action.setId(a.getId());
					break;
				}
			}
			if(!actionFound) session.save(action);
		}
		
		// For each parameter
		for (Parameter parameter : method.getParameters()) {
			if (parameter.isAnnotationPresent(ActionFieldTag.class)) {
				ActionField actionField = new ActionField();
				actionField.setAction(action);
				actionField.setParameter(parameter.getName());
				ActionFieldTag actionFieldTag = parameter.getAnnotation(ActionFieldTag.class);
				actionField.setName(actionFieldTag.name());
				actionField.setDescription(actionFieldTag.description());
				
				if(actionFields == null ) session.save(actionField);
				else
				{
					
					boolean actionFieldFound = false;
					for(ActionField af : actionFields){
						if(af.getAction().getId() == actionField.getAction().getId() &&
								af.getName().equals(actionField.getName()) ) 
							actionFieldFound = true;
					}
					if(!actionFieldFound) session.save(actionField);
				}
				
			}
		}
	}
}
