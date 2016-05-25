package iftttclone.config;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import iftttclone.channels.Scheduler;
import iftttclone.entities.*;

@Configuration
@EnableWebMvc
@ComponentScan("iftttclone.controllers")
public class WebConfig {
	
	@Bean
	public SessionFactory sessionFactory() {
		// Create the SessionFactory from hibernate.cfg.xml
		org.hibernate.cfg.Configuration conf = new org.hibernate.cfg.Configuration().configure();
		
		// XXX add entities
		conf.addAnnotatedClass(Action.class);
		conf.addAnnotatedClass(ActionField.class);
		conf.addAnnotatedClass(Channel.class);
		conf.addAnnotatedClass(ChannelConnector.class);
		conf.addAnnotatedClass(Ingredient.class);
		conf.addAnnotatedClass(PublicRecipe.class);
		conf.addAnnotatedClass(PublicRecipeActionField.class);
		conf.addAnnotatedClass(PublicRecipeTriggerField.class);
		conf.addAnnotatedClass(Recipe.class);
		conf.addAnnotatedClass(RecipeActionField.class);
		conf.addAnnotatedClass(RecipeLog.class);
		conf.addAnnotatedClass(RecipeTriggerField.class);
		conf.addAnnotatedClass(Trigger.class);
		conf.addAnnotatedClass(TriggerField.class);
		conf.addAnnotatedClass(User.class);

		ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
				.applySettings(conf.getProperties())
				.build();
		
		return conf.buildSessionFactory(serviceRegistry);
	}
	
	@Bean
	public Scheduler scheduler() {
		return new Scheduler();
	}
}