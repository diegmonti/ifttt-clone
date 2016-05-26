package iftttclone.channels;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import iftttclone.channels.annotation.ActionFieldTag;
import iftttclone.channels.annotation.ActionTag;
import iftttclone.channels.annotation.ChannelTag;
import iftttclone.channels.annotation.IngredientTag;
import iftttclone.channels.annotation.TriggerFieldTag;
import iftttclone.channels.annotation.TriggerTag;

@ChannelTag(name="GoogleCalendar", description = "The channel for google calendar")
public class GCalendarChannel {


	
	
	@TriggerTag(name = "EventStarted", description = "This trigger fires when an event starts")
	public Map<String, String> newEventStarted(
			@TriggerFieldTag(name = "eventTitle", description = "the title of the event") String title,
			@TriggerFieldTag(name = "eventDescription", description = "the description of the event") String subject,
			@TriggerFieldTag(name = "eventLocation", description = "the location of the event") String location,
			Date lastRun) {
		
		// TODO: do something
		
		Map<String, String> ingredients = new HashMap<String, String>();
		return ingredients;
	}
	
	@TriggerTag(name = "EventAdded", description = "This trigger fires when an event is added")
	public Map<String, String> newEventAdded(
			@TriggerFieldTag(name = "Event Title", description = "the title of the event") String title,
			@TriggerFieldTag(name = "Event Description", description = "the description of the event") String subject,
			@TriggerFieldTag(name = "Event Location", description = "the location of the event") String location,
			Date lastRun) {
		
		// TODO: do something
		
		Map<String, String> ingredients = new HashMap<String, String>();
		return ingredients;
	}
	
	@ActionTag(name = "CreateEvent", description = "Creates a new Event")
	public void createEvent(
			@ActionFieldTag(name = "Event Title", description = "Title of the event") String title,
			@ActionFieldTag(name = "Event DateTime", description = "Date and time of the new event") Date dateTime) {

		
	}
	
	
}

