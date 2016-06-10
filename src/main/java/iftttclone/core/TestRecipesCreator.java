package iftttclone.core;


import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;

import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeActionField;
import iftttclone.entities.RecipeTriggerField;
import iftttclone.entities.User;
import iftttclone.repositories.ActionRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.RecipeRepository;
import iftttclone.repositories.TriggerRepository;
import iftttclone.repositories.UserRepository;

@Component
public class TestRecipesCreator {
	@Autowired
	private ChannelRepository channels;
	@Autowired
	private TriggerRepository triggers;
	@Autowired
	private ActionRepository actions;
	@Autowired
	private UserRepository users;
	@Autowired
	private RecipeRepository recipes;
	private boolean weatherTestsDone;
	
	@TransactionalEventListener()
	public void createTests(TestEvent event){
		System.err.println("-CHANNEL_TESTS: begin");
		
		User user = users.getUserByUsername("testUser");
		if(user == null){
			System.err.println("-CHANNEL_TESTS: creating user");
			user = new User();
			user.setUsername("testUser");
			user.setPassword("password");
			user.setEmail("user.test@gmail.com");
			user.setTimezone("UTC");
			users.save(user);
		}
			
		this.weatherTests(user);
		
		System.err.println("-CHANNEL_TESTS: end");
	}
	
	@Transactional
	public void weatherTests(User user){
		if(this.weatherTestsDone){
			return;
		}
		
		System.err.println("--WEATHER_TESTS: begin");
		
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		
		
		System.err.println("--WEATHER_TESTS: creating tomorrowReport");
		Recipe r = this.createBasicRecipe(user, now);
		r.setTitle("tomorrowReport");
		
		r.setTrigger(triggers.getTriggerByMethodAndChannel("tomorrowWeatherReport", channels.getChannelByClasspath("iftttclone.channels.WeatherChannel")));
		
		Map<String, RecipeTriggerField> rtfs = new HashMap<String, RecipeTriggerField>();
		RecipeTriggerField rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue("24358");
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue( Integer.toString(now.get(Calendar.HOUR_OF_DAY)) );
		rtfs.put("arg1", rtf);
		rtf = this.createBasicRecipeTriggerField(2, r);
		rtf.setValue( Integer.toString(now.get(Calendar.MINUTE)+2) );
		rtfs.put("arg2", rtf);
		
		Map<String, RecipeActionField> rafs = new HashMap<String, RecipeActionField>();
		RecipeActionField raf = this.createBasicRecipeActionField(r);
		raf.setValue("CurrTempFahrenheit: {{CurrTempFahrenheit}}\nCurrTempCelsius: {{CurrTempCelsius}}"
				+ "\nCondition: {{Condition}}\nHighTempFahrenheit: {{HighTempFahrenheit}}"
				+ "\nHighTempCelsius: {{HighTempCelsius}}\nLowTempFahrenheit: {{LowTempFahrenheit}}"
				+ "\nLowTempCelsius: {{LowTempCelsius}}\nSunriseTime: {{SunriseTime}}\nCheckTime: {{CheckTime}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		// Both temperatures, Celsius
		System.err.println("--WEATHER_TESTS: creating currentTemperature1");
		r = this.createBasicRecipe(user, now);
		r.setTitle("currentTemperature1");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("currentTemperature", channels.getChannelByClasspath("iftttclone.channels.WeatherChannel")));
		
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue("24358");
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue("12");
		rtfs.put("arg1", rtf);
		rtf = this.createBasicRecipeTriggerField(2, r);
		rtf.setValue("30");
		rtfs.put("arg2", rtf);
		rtf = this.createBasicRecipeTriggerField(3, r);
		rtf.setValue("c");
		rtfs.put("arg3", rtf);
		
		raf = this.createBasicRecipeActionField(r);
		raf.setValue("CurrTempFahrenheit: {{CurrTempFahrenheit}}\nCurrTempCelsius: {{CurrTempCelsius}}"
				+ "\nCondition: {{Condition}}\nCheckTime: {{CheckTime}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		// One temperatures, Fahrenheit
		System.err.println("--WEATHER_TESTS: creating currentTemperature2");
		r = this.createBasicRecipe(user, now);
		r.setTitle("currentTemperature2");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("currentTemperature", channels.getChannelByClasspath("iftttclone.channels.WeatherChannel")));
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue("24358");
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue("");
		rtfs.put("arg1", rtf);
		rtf = this.createBasicRecipeTriggerField(2, r);
		rtf.setValue("80");
		rtfs.put("arg2", rtf);
		rtf = this.createBasicRecipeTriggerField(3, r);
		rtf.setValue("f");
		rtfs.put("arg3", rtf);
		
		raf = this.createBasicRecipeActionField(r);
		raf.setValue("CurrTempFahrenheit: {{CurrTempFahrenheit}}\nCurrTempCelsius: {{CurrTempCelsius}}"
				+ "\nCondition: {{Condition}}\nCheckTime: {{CheckTime}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		System.err.println("--WEATHER_TESTS: creating sunrise");
		r = this.createBasicRecipe(user, now);
		r.setTitle("sunrise");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("sunrise", channels.getChannelByClasspath("iftttclone.channels.WeatherChannel")));
		
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue("24358");
		rtfs.put("arg0", rtf);
		
		rafs.clear();
		raf = this.createBasicRecipeActionField(r);
		raf.setValue("CurrTempFahrenheit: {{CurrTempFahrenheit}}\nCurrTempCelsius: {{CurrTempCelsius}}"
				+ "\nCondition: {{Condition}}\nHighTempFahrenheit: {{HighTempFahrenheit}}"
				+ "\nHighTempCelsius: {{HighTempCelsius}}\nLowTempFahrenheit: {{LowTempFahrenheit}}"
				+ "\nLowTempCelsius: {{LowTempCelsius}}\nCheckTime: {{CheckTime}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		System.err.println("--WEATHER_TESTS: end");
		
		this.weatherTestsDone = true;
	}
	
	private Recipe createBasicRecipe(User user, Calendar now){
		Recipe r = new Recipe();
		r.setActive(true);
		r.setRuns(new Integer(0));
		r.setCreationTime(now.getTime());
		r.setLastRun(now.getTime());
		r.setUser(user);
		r.setAction(actions.getActionByMethodAndChannel("simpleAction", channels.getChannelByClasspath("iftttclone.channels.TestChannel")));
		return r;
	}
	
	private RecipeTriggerField createBasicRecipeTriggerField(int i, Recipe r){
		RecipeTriggerField rtf = new RecipeTriggerField();
		rtf.setParameter("arg"+i);
		rtf.setRecipe(r);
		return rtf;
	}
	
	private RecipeActionField createBasicRecipeActionField(Recipe r){
		RecipeActionField raf = new RecipeActionField();
		raf.setParameter("arg0");
		raf.setRecipe(r);
		return raf;
	}
	
}