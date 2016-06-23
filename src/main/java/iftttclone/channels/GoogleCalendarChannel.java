package iftttclone.channels;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.FieldTag;
import iftttclone.channels.annotations.TriggerTag;
import iftttclone.core.Validator;
import iftttclone.core.Validator.FieldType;

@ChannelTag(name = "Google Calendar", description = "The channel for google calendar", withConnection = true)
public class GoogleCalendarChannel extends AbstractChannel {

	@TriggerTag(name = "EventStarted", description = "This trigger fires when an event starts, it can be filtered with some keywords in an inclusive OR fashion. Fired if the keyword is present, left blank if not needed")
	@IngredientTag(name = "WhenStarts", description = "When the event starts", example = "23/05/2016 13:09")
	@IngredientTag(name = "WhenEnds", description = "When the event ends", example = "23/05/2016 15:09")
	@IngredientTag(name = "Title", description = "The title of the event", example = "Important exam")
	@IngredientTag(name = "Description", description = "The description of the event", example = "That course I hate")
	@IngredientTag(name = "Where", description = "The location of the event", example = "10I")
	public List<Map<String, String>> newEventStarted(
			@FieldTag(name = "titleKeyword", description = "A keyword to search in the title of the event", type = FieldType.NULLABLETEXT) String titleKW,
			@FieldTag(name = "DescriptionKeyword", description = "A keyword to search in the description of the event", type = FieldType.NULLABLETEXT) String descriptionKW,
			@FieldTag(name = "locationKeyword", description = "A keyword to search in the location of the event", type = FieldType.NULLABLETEXT, publishable = false) String locationKW) {

		Events events;
		try {
			Calendar gCalendar = this.getCalendarService();
			
			events = gCalendar.events().list("primary").setSingleEvents(true).setTimeZone(this.getUser().getTimezone())
					.setTimeMin(new DateTime(this.getLastRun())).setOrderBy("startTime").execute();
		} catch (GeneralSecurityException | IOException e) {	// error 500
			System.err.println("******ERROR******");
			e.printStackTrace();
			return null;
		}

		Date now = java.util.Calendar.getInstance(TimeZone.getTimeZone(this.getUser().getTimezone())).getTime();	// now for the user
		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		for (Event event : events.getItems()) {
			if(!this.filterEventOr(titleKW, descriptionKW, locationKW, 
					event.getSummary(), event.getDescription(), event.getLocation())){	// change Or for And to get other trigger
				continue;
			}
			
			DateTime start = event.getStart().getDateTime();
			if (start == null) {	// this is not supposed to happen but it is in the example code so I leave it
				start = event.getStart().getDate();
			}
			if(start == null){
				continue;
			}
			Date startDate = new Date(start.getValue());
			if(startDate.after(now)){	// early termination, further events have not started since they are ordered
				break;
			}
			if(this.getLastRun().after(startDate)){	// already processed, this may skip some events that were created while executing this function
				continue;
			}
			
			Map<String, String> resEntry = new HashMap<String, String>();
			this.addIngredients(event, resEntry);
			result.add(resEntry);
		}

		return result;
	}

	@TriggerTag(name = "EventAdded", description = "This trigger fires when an event is added, it can be filtered with some keywords in an inclusive OR fashion. Fired if the keyword is present, left blank if not needed")
	@IngredientTag(name = "WhenStarts", description = "When the event starts", example = "23/05/2016 13:09")
	@IngredientTag(name = "WhenEnds", description = "When the event ends", example = "23/05/2016 15:09")
	@IngredientTag(name = "Title", description = "The title of the event", example = "Important exam")
	@IngredientTag(name = "Description", description = "The description of the event", example = "That course I hate")
	@IngredientTag(name = "Where", description = "The location of the event", example = "10I")
	public List<Map<String, String>> newEventAdded(
			@FieldTag(name = "titleKeyword", description = "A keyword to search in the title of the event", type = FieldType.NULLABLETEXT) String titleKW,
			@FieldTag(name = "DescriptionKeyword", description = "A keyword to search in the description of the event", type = FieldType.NULLABLETEXT) String descriptionKW,
			@FieldTag(name = "locationKeyword", description = "A keyword to search in the location of the event", type = FieldType.NULLABLETEXT, publishable = false) String locationKW) {

		Events events;
		try {
			Calendar gCalendar = this.getCalendarService();
			
			events = gCalendar.events().list("primary").setSingleEvents(true).setTimeZone(this.getUser().getTimezone())
					.setOrderBy("updated").execute();
		} catch (GeneralSecurityException | IOException e) {	// error 500
			System.err.println("******ERROR******");
			e.printStackTrace();
			return null;
		}

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		ListIterator<Event> li = events.getItems().listIterator(events.getItems().size());
		while(li.hasPrevious()){	// backwards for early termination
			Event event = li.previous();
			
			if(!this.filterEventOr(titleKW, descriptionKW, locationKW, 
					event.getSummary(), event.getDescription(), event.getLocation())){	// change Or for And to get other trigger
				continue;
			}
			
			DateTime creation = event.getCreated();
			if (creation == null) {	// this should not happen
				continue;
			}
			Date creationDate = new Date(creation.getValue());
			if(this.getLastRun().after(creationDate)){	// already processed, this may skip some events that were created while executing this function
				continue;
			}
			
			DateTime update = event.getUpdated();
			if (update != null) {	// this should always happen
				Date updateDate = new Date(update.getValue());
				if(updateDate.before(this.getLastRun())){	// early termination, further events have not beign modified
					break;
				}
			}
			
			Map<String, String> resEntry = new HashMap<String, String>();
			this.addIngredients(event, resEntry);
			result.add(resEntry);
		}

		return result;
	}

	@ActionTag(name = "CreateEvent", description = "Creates a new Event")
	public void createEvent(
			@FieldTag(name = "Title", description = "The title of the event", type = FieldType.TEXT) String title,
			@FieldTag(name = "Description", description = "The description of the event", type = FieldType.TEXT) String description,
			@FieldTag(name = "Where", description = "The location of the event", type = FieldType.TEXT, publishable = false) String location,
			@FieldTag(name = "WhenStarts", description = "When the event starts", type = FieldType.TIMESTAMP, publishable = false) String starts,
			@FieldTag(name = "WhenEnds", description = "When the event ends", type = FieldType.TIMESTAMP, publishable = false) String ends) {
		
		Event event = new Event();
		event.setSummary(title);
		event.setDescription(description);
		event.setLocation(location);
		DateFormat fromFormat = new SimpleDateFormat(Validator.TIMESTAMP_FORMAT);
		fromFormat.setTimeZone(TimeZone.getTimeZone(this.getUser().getTimezone()));
		Date startDate;
		try {
			startDate = fromFormat.parse(starts);
		} catch (ParseException e1) {	// error 500, should not happen
			return;
		}
		if(startDate == null){	// error 500, should not happen
			return;
		}
		DateTime startDateTime = new DateTime(startDate, TimeZone.getTimeZone(this.getUser().getTimezone()));
		EventDateTime startEDT = new EventDateTime().setDateTime(startDateTime).setTimeZone(this.getUser().getTimezone());
		event.setStart(startEDT);
		Date endDate;
		try {
			endDate = fromFormat.parse(ends);
		} catch (ParseException e1) {	// error 500, should not happen
			return;
		}
		if(endDate == null){	// error 500, should not happen
			return;
		}
		DateTime endDateTime = new DateTime(endDate, TimeZone.getTimeZone(this.getUser().getTimezone()));
		EventDateTime endEDT = new EventDateTime().setDateTime(endDateTime).setTimeZone(this.getUser().getTimezone());
		event.setEnd(endEDT);
		
		try {
			Calendar gCalendar = this.getCalendarService();
			gCalendar.events().insert("primary", event).execute();
		} catch (GeneralSecurityException | IOException e) {	// error 500
			System.err.println("******ERROR******");
			e.printStackTrace();
			return;
		}
	}
	
	
	// Utility methods
	private Calendar getCalendarService() throws GeneralSecurityException, IOException{
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		Credential credentials = new GoogleCredential().setAccessToken(this.getChannelConnector().getToken());
		return new Calendar.Builder(httpTransport, jsonFactory, credentials).setApplicationName("IFTTT-CLONE")
				.build();
	}
	
	private boolean filterEventOr(String titleKW, String descriptionKW, String locationKW, 
			String title, String description, String location){
		if((titleKW.isEmpty()) && (descriptionKW.isEmpty()) && (locationKW.isEmpty())){
			return true;
		}
		if((title != null) && (!titleKW.isEmpty()) && (title.contains(titleKW))){
			return true;
		}
		if((description != null) && (!descriptionKW.isEmpty()) && (description.contains(descriptionKW))){
			return true;
		}
		if((location != null) && (!locationKW.isEmpty()) && (location.contains(locationKW))){
			return true;
		}
		return false;
	}
	
	private boolean filterEventAnd(String titleKW, String descriptionKW, String locationKW, 
			String title, String description, String location){
		if((titleKW.isEmpty()) && (descriptionKW.isEmpty()) && (locationKW.isEmpty())){
			return true;
		}
		if((title != null) && (!titleKW.isEmpty()) && (!title.contains(titleKW))){
			return false;
		}
		if((description != null) && (!descriptionKW.isEmpty()) && (!description.contains(descriptionKW))){
			return false;
		}
		if((location != null) && (!locationKW.isEmpty()) && (!location.contains(locationKW))){
			return false;
		}
		return true;
	}
	
	private void addIngredients(Event event, Map<String, String> ingredients){
		DateFormat toFormat = new SimpleDateFormat("dd MMM yyyy HH:mm z", Locale.ENGLISH);
		
		DateTime start = event.getStart().getDateTime();
		if (start == null) {	// this is not supposed to happen but it is in the example code so I leave it
			start = event.getStart().getDate();
		}
		Date startDate;
		if(start != null){
			startDate = new Date(start.getValue());
		} else {
			startDate = java.util.Calendar.getInstance().getTime();
		}
		
		DateTime end = event.getEnd().getDateTime();
		if (end == null) {	// this is not supposed to happen but it is in the example code so I leave it
			end = event.getStart().getDate();
		}
		Date endDate;
		if(end != null){
			endDate = new Date(end.getValue());
		} else {
			endDate = java.util.Calendar.getInstance().getTime();
		}
		
		String title = event.getSummary();
		if(title == null){
			title = "";
		}
		
		String description = event.getDescription();
		if(description == null){
			description = "";
		}
		
		String location = event.getLocation();
		if(location == null){
			location = "";
		}
		
		ingredients.put("WhenStarts", toFormat.format(startDate));
		ingredients.put("WhenEnds", toFormat.format(endDate));
		ingredients.put("Title", title);
		ingredients.put("Description", description);
		ingredients.put("Where", location);
	}

}
