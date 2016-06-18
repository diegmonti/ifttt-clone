package iftttclone.core;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;
import iftttclone.entities.ChannelConnector;
import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeActionField;
import iftttclone.entities.RecipeTriggerField;
import iftttclone.entities.User;
import iftttclone.repositories.ActionRepository;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.RecipeRepository;
import iftttclone.repositories.TriggerRepository;
import iftttclone.repositories.UserRepository;

@Component
@TestPropertySource(value = { "classpath:application.properties" })
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
	@Autowired
	private ChannelConnectorRepository channelConnectors;
	@Autowired
	private Environment env;
	private boolean weatherTestsDone;
	private boolean gCalendarTestsDone;
	private boolean gMailTestsDone;
	
	
	@Transactional
	public void createTests(){
		System.err.println("-CHANNEL_TESTS: begin");
		
		User user = users.getUserByUsername("user");
		if(user == null){
			System.err.println("-CHANNEL_TESTS: creating user");
			user = new User();
			user.setUsername("user");
			user.setPassword("$2a$10$nnLzeVdmP9OSKJTUqAtpBueKWZXJACcYiFZ0PCc30P.szKVp6iB4m"); // "password"
			user.setEmail("user.test@gmail.com");
			user.setTimezone("UTC");
			users.save(user);
		}
			
		//this.weatherTests(user);
		//this.gCalendarTests(user);
		//this.gMailTests(user);
		
		System.err.println("-CHANNEL_TESTS: end");
	}
	
	
	private void weatherTests(User user){
		if(this.weatherTestsDone){
			return;
		}
		
		System.err.println("--WEATHER_TESTS: begin");
		
		Calendar now = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		Calendar yesterday = (Calendar) now.clone();
		yesterday.add(Calendar.DAY_OF_MONTH, -1);
		String location = "24358";
		Channel channel = channels.getChannelByClasspath("iftttclone.channels.WeatherChannel");
		Action action = actions.getActionByMethodAndChannel("simpleAction", channels.getChannelByClasspath("iftttclone.channels.TestChannel"));
		
		
		System.err.println("--WEATHER_TESTS: creating tomorrowReport");	// should run once
		Recipe r = this.createBasicRecipe(user, now, action);
		r.setTitle("tomorrowReport");
		
		r.setTrigger(triggers.getTriggerByMethodAndChannel("tomorrowWeatherReport", channel));
		
		Map<String, RecipeTriggerField> rtfs = new HashMap<String, RecipeTriggerField>();
		RecipeTriggerField rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue(location);
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue( Integer.toString(now.get(Calendar.HOUR_OF_DAY)) );
		rtfs.put("arg1", rtf);
		rtf = this.createBasicRecipeTriggerField(2, r);
		rtf.setValue( Integer.toString(now.get(Calendar.MINUTE)+1) );
		rtfs.put("arg2", rtf);
		
		Map<String, RecipeActionField> rafs = new HashMap<String, RecipeActionField>();
		RecipeActionField raf = this.createBasicRecipeActionField(0, r);
		raf.setValue("CurrTempFahrenheit: {{CurrTempFahrenheit}}\nCurrTempCelsius: {{CurrTempCelsius}}"
				+ "\nCondition: {{Condition}}\nHighTempFahrenheit: {{HighTempFahrenheit}}"
				+ "\nHighTempCelsius: {{HighTempCelsius}}\nLowTempFahrenheit: {{LowTempFahrenheit}}"
				+ "\nLowTempCelsius: {{LowTempCelsius}}\nSunriseTime: {{SunriseTime}}\nCheckTime: {{CheckTime}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		// Both temperatures, Celsius
		System.err.println("--WEATHER_TESTS: creating currentTemperature1");	// should always run
		r = this.createBasicRecipe(user, now, action);
		r.setTitle("currentTemperature1");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("currentTemperature", channel));
		
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue(location);
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
		
		raf = this.createBasicRecipeActionField(0, r);
		raf.setValue("CurrTempFahrenheit: {{CurrTempFahrenheit}}\nCurrTempCelsius: {{CurrTempCelsius}}"
				+ "\nCondition: {{Condition}}\nCheckTime: {{CheckTime}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		// One temperatures, Fahrenheit
		System.err.println("--WEATHER_TESTS: creating currentTemperature2");	// should always run
		r = this.createBasicRecipe(user, now, action);
		r.setTitle("currentTemperature2");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("currentTemperature", channel));
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue(location);
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
		
		raf = this.createBasicRecipeActionField(0, r);
		raf.setValue("CurrTempFahrenheit: {{CurrTempFahrenheit}}\nCurrTempCelsius: {{CurrTempCelsius}}"
				+ "\nCondition: {{Condition}}\nCheckTime: {{CheckTime}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		// Null intersection
		System.err.println("--WEATHER_TESTS: creating currentTemperature3");	// should never run
		r = this.createBasicRecipe(user, now, action);
		r.setTitle("currentTemperature3");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("currentTemperature", channel));
		
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue(location);
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue("22");
		rtfs.put("arg1", rtf);
		rtf = this.createBasicRecipeTriggerField(2, r);
		rtf.setValue("18");
		rtfs.put("arg2", rtf);
		rtf = this.createBasicRecipeTriggerField(3, r);
		rtf.setValue("c");
		rtfs.put("arg3", rtf);
		
		raf = this.createBasicRecipeActionField(0, r);
		raf.setValue("CurrTempFahrenheit: {{CurrTempFahrenheit}}\nCurrTempCelsius: {{CurrTempCelsius}}"
				+ "\nCondition: {{Condition}}\nCheckTime: {{CheckTime}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		System.err.println("--WEATHER_TESTS: creating sunrise");	// should run once
		r = this.createBasicRecipe(user, yesterday, action);
		r.setTitle("sunrise");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("sunrise", channel));
		
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue(location);
		rtfs.put("arg0", rtf);
		
		rafs.clear();
		raf = this.createBasicRecipeActionField(0, r);
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
	
	
	/*Create events with start=end in the range of execution (first preferably), all (trigger) should run once
	* Just create after yesterday at this time and then run
	* Value							place			caught by
	* A title						title			newEventStarted2, newEventStarted
	* A title, Some place			title,location	newEventStarted2, newEventStarted, newEventAdded2
	* place							location		newEventStarted2
	* test							any				newEventStarted2
	* The description of A title	title			newEventStarted2, newEventStarted, newEventAdded
	* The description of A title	location		newEventStarted2, newEventAdded
	 */
	private void gCalendarTests(User user){
		if(this.gCalendarTestsDone){
			return;
		}
		
		System.err.println("--GOOGLE_CALENDAR_TESTS: begin");
		
		System.err.println("--GOOGLE_CALENDAR_TESTS: creating connection");
		String token = env.getProperty("gCalendar.token");
		String refreshToken = env.getProperty("gCalendar.refreshToken");
		ChannelConnector cc = new ChannelConnector();
		cc.setToken(token);
		cc.setRefreshToken(refreshToken);
		cc.setUser(user);
		Channel channel = channels.getChannelByClasspath("iftttclone.channels.GoogleCalendarChannel");
		cc.setChannel(channel);
		Calendar yesterday = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		yesterday.add(Calendar.DATE, -1);
		cc.setConnectionTime(yesterday.getTimeInMillis());
		channelConnectors.save(cc);
		
		Calendar start = (Calendar) yesterday.clone();
		start.add(Calendar.HOUR, 1);
		Calendar end = (Calendar) start.clone();
		end.add(Calendar.HOUR, 1);
		String title = "A title";
		String description = "The description of " + title;
		String location = "Some place";
		DateFormat actionFormat = new SimpleDateFormat("dd MMM yyyy HH:mm", Locale.ENGLISH);
		
		Action action = actions.getActionByMethodAndChannel("simpleAction", channels.getChannelByClasspath("iftttclone.channels.TestChannel"));
		
		
		// matches title or location
		System.err.println("--GOOGLE_CALENDAR_TESTS: creating newEventStarted");
		Recipe r = this.createBasicRecipe(user, yesterday, action);
		r.setTitle("newEventStarted");
		
		r.setTrigger(triggers.getTriggerByMethodAndChannel("newEventStarted", channel));
		
		Map<String, RecipeTriggerField> rtfs = new HashMap<String, RecipeTriggerField>();
		RecipeTriggerField rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue(title);
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue("");
		rtfs.put("arg1", rtf);
		rtf = this.createBasicRecipeTriggerField(2, r);
		rtf.setValue(location);
		rtfs.put("arg2", rtf);
		
		Map<String, RecipeActionField> rafs = new HashMap<String, RecipeActionField>();
		RecipeActionField raf = this.createBasicRecipeActionField(0, r);
		raf.setValue("WhenStarts: {{WhenStarts}}\nWhenEnds: {{WhenEnds}}"
				+ "\nTitle: {{Title}}\nDescription: {{Description}}"
				+ "\nWhere: {{Where}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		// unfiltered
		System.err.println("--GOOGLE_CALENDAR_TESTS: creating newEventStarted2");
		r = this.createBasicRecipe(user, yesterday, action);
		r.setTitle("newEventStarted2");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("newEventStarted", channel));
		
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue("");
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue("");
		rtfs.put("arg1", rtf);
		rtf = this.createBasicRecipeTriggerField(2, r);
		rtf.setValue("");
		rtfs.put("arg2", rtf);
		
		raf = this.createBasicRecipeActionField(0, r);
		raf.setValue("WhenStarts: {{WhenStarts}}\nWhenEnds: {{WhenEnds}}"
				+ "\nTitle: {{Title}}\nDescription: {{Description}}"
				+ "\nWhere: {{Where}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		// matches description in any field
		System.err.println("--GOOGLE_CALENDAR_TESTS: creating newEventAdded");
		r = this.createBasicRecipe(user, yesterday, action);
		r.setTitle("newEventAdded");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("newEventAdded", channel));
		
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue(description);
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue(description);
		rtfs.put("arg1", rtf);
		rtf = this.createBasicRecipeTriggerField(2, r);
		rtf.setValue(description);
		rtfs.put("arg2", rtf);
		
		raf = this.createBasicRecipeActionField(0, r);
		raf.setValue("WhenStarts: {{WhenStarts}}\nWhenEnds: {{WhenEnds}}"
				+ "\nTitle: {{Title}}\nDescription: {{Description}}"
				+ "\nWhere: {{Where}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		// matches only location
		System.err.println("--GOOGLE_CALENDAR_TESTS: creating newEventAdded2");
		r = this.createBasicRecipe(user, yesterday, action);
		r.setTitle("newEventAdded2");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("newEventAdded", channel));
		
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue("");
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue("");
		rtfs.put("arg1", rtf);
		rtf = this.createBasicRecipeTriggerField(2, r);
		rtf.setValue(location);
		rtfs.put("arg2", rtf);
		
		raf = this.createBasicRecipeActionField(0, r);
		raf.setValue("WhenStarts: {{WhenStarts}}\nWhenEnds: {{WhenEnds}}"
				+ "\nTitle: {{Title}}\nDescription: {{Description}}"
				+ "\nWhere: {{Where}}");
		rafs.put("arg0", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
		
		// create event
		System.err.println("--GOOGLE_CALENDAR_TESTS: creating createEvent");	// always done
		r = this.createBasicRecipe(user, yesterday, actions.getActionByMethodAndChannel("createEvent", channel));
		r.setTitle("createEvent");
		r.setTrigger(triggers.getTriggerByMethodAndChannel("simpleTrigger", channels.getChannelByClasspath("iftttclone.channels.TestChannel")));
		
		rtfs.clear();
		rtf = this.createBasicRecipeTriggerField(0, r);
		rtf.setValue("Value of simpleTrigger");
		rtfs.put("arg0", rtf);
		rtf = this.createBasicRecipeTriggerField(1, r);
		rtf.setValue("yes");
		rtfs.put("arg1", rtf);
		
		raf = this.createBasicRecipeActionField(0, r);
		raf.setValue("{{Key}}");
		rafs.put("arg0", raf);
		raf = this.createBasicRecipeActionField(1, r);
		raf.setValue("{{Key1}}, {{Key2}}");
		rafs.put("arg1", raf);
		raf = this.createBasicRecipeActionField(2, r);
		raf.setValue("Location");
		rafs.put("arg2", raf);
		raf = this.createBasicRecipeActionField(3, r);
		raf.setValue( actionFormat.format(start.getTime()) );
		rafs.put("arg3", raf);
		raf = this.createBasicRecipeActionField(4, r);
		raf.setValue( actionFormat.format(end.getTime()) );
		rafs.put("arg4", raf);
		
		r.setRecipeTriggerFields(rtfs);
		r.setRecipeActionFields(rafs);
		recipes.save(r);
		
				
		System.err.println("--GOOGLE_CALENDAR_TESTS: end");
		
		this.gCalendarTestsDone = true;
	}
	
	
	// Utility methods
	private Recipe createBasicRecipe(User user, Calendar initTime, Action action){
		Recipe r = new Recipe();
		r.setActive(true);
		r.setRuns(new Integer(0));
		r.setCreationTime(initTime.getTimeInMillis());
		r.setLastRun(initTime.getTimeInMillis());
		r.setUser(user);
		r.setAction(action);
		return r;
	}
	
	private RecipeTriggerField createBasicRecipeTriggerField(int i, Recipe r){
		RecipeTriggerField rtf = new RecipeTriggerField();
		rtf.setParameter("arg"+i);
		rtf.setRecipe(r);
		return rtf;
	}
	
	private RecipeActionField createBasicRecipeActionField(int i, Recipe r){
		RecipeActionField raf = new RecipeActionField();
		raf.setParameter("arg"+i);
		raf.setRecipe(r);
		return raf;
	}
	
}