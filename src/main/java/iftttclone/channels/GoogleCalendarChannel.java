package iftttclone.channels;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.TimeZone;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
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
import iftttclone.exceptions.SchedulerException;

@ChannelTag(name = "Google Calendar", description = "Google Calendar is a time-management web application created by Google.", withConnection = true, permits = "Read and write the primary calendar")
public class GoogleCalendarChannel extends AbstractChannel {

	@TriggerTag(name = "Event starts", description = "This trigger fires within 15 minutes of the starting time of an event. It is optionally possible to filter the events by setting a keyword or phrase.")
	@IngredientTag(name = "Starts", description = "Date and time the event starts.", example = "23/05/2016 13:00")
	@IngredientTag(name = "Ends", description = "Date and time the event ends.", example = "23/05/2016 15:00")
	@IngredientTag(name = "Title", description = "The title of the event.", example = "Important exam")
	@IngredientTag(name = "Description", description = "The description of the event.", example = "A course that I hate")
	@IngredientTag(name = "Where", description = "The location of the event.", example = "Room 10I")
	public List<Map<String, String>> newEventStarted(
			@FieldTag(name = "Keyword or phrase", description = "A keyword to search in the event's title, description or location.", type = FieldType.NULLABLETEXT) String keyword) {

		Events events;
		try {
			Calendar gCalendar = this.getCalendarService();

			events = gCalendar.events().list("primary").setSingleEvents(true).setTimeZone(this.getUser().getTimezone())
					.setTimeMin(new DateTime(this.getLastRun())).setOrderBy("startTime").execute();
		} catch (GeneralSecurityException | IOException e) {
			throw new SchedulerException();
		}

		Date now = new Date();
		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		if (events.getItems() == null) {
			return null;
		}

		for (Event event : events.getItems()) {
			// This event does not contain the keyword
			if (!this.filterEvent(keyword, event)) {
				continue;
			}

			DateTime start = event.getStart().getDateTime();
			// This is not supposed to happen
			if (start == null) {
				start = event.getStart().getDate();
			}
			if (start == null) {
				continue;
			}

			Date startDate = new Date(start.getValue());
			// Early termination, further events have not started since they are
			// ordered
			if (startDate.after(now)) {
				break;
			}

			// Already processed, this may skip some events that were created
			// while executing this function
			if (this.getLastRun().after(startDate)) {
				continue;
			}

			Map<String, String> resEntry = new HashMap<String, String>();
			this.addIngredients(event, resEntry);
			result.add(resEntry);
		}

		return result;
	}

	@TriggerTag(name = "New event added", description = "This trigger fires when an new event is added. It is optionally possible to filter the events by setting a keyword or phrase.")
	@IngredientTag(name = "Starts", description = "Date and time the event starts.", example = "23/05/2016 13:00")
	@IngredientTag(name = "Ends", description = "Date and time the event ends.", example = "23/05/2016 15:00")
	@IngredientTag(name = "Title", description = "The title of the event.", example = "Important exam")
	@IngredientTag(name = "Description", description = "The description of the event.", example = "A course that I hate")
	@IngredientTag(name = "Where", description = "The location of the event.", example = "Room 10I")
	public List<Map<String, String>> newEventAdded(
			@FieldTag(name = "Keyword or phrase", description = "A keyword to search in the event's title, description or location.", type = FieldType.NULLABLETEXT) String keyword) {

		Events events;

		try {
			Calendar gCalendar = this.getCalendarService();

			events = gCalendar.events().list("primary").setSingleEvents(true).setTimeZone(this.getUser().getTimezone())
					.setOrderBy("updated").execute();
		} catch (GeneralSecurityException | IOException e) {
			throw new SchedulerException();
		}

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		if (events.getItems() == null) {
			return null;
		}

		ListIterator<Event> li = events.getItems().listIterator(events.getItems().size());

		// Backwards for early termination
		while (li.hasPrevious()) {
			Event event = li.previous();

			if (!this.filterEvent(keyword, event)) {
				continue;
			}

			DateTime creation = event.getCreated();
			// This should not happen
			if (creation == null) {
				continue;
			}

			Date creationDate = new Date(creation.getValue());
			// Already processed, this may skip some events that were created
			// while executing this function
			if (this.getLastRun().after(creationDate)) {
				continue;
			}

			DateTime update = event.getUpdated();

			// This should always happen
			if (update != null) {
				Date updateDate = new Date(update.getValue());
				// Early termination, further events have not being modified
				if (updateDate.before(this.getLastRun())) {
					break;
				}
			}

			Map<String, String> resEntry = new HashMap<String, String>();
			this.addIngredients(event, resEntry);
			result.add(resEntry);
		}

		return result;
	}

	@ActionTag(name = "Create event", description = "This action will add a new event to Google Calendar.")
	public void createEvent(
			@FieldTag(name = "Title", description = "The title of the event.", type = FieldType.TEXT) String title,
			@FieldTag(name = "Description", description = "The description of the event.", type = FieldType.NULLABLETEXT) String description,
			@FieldTag(name = "Location", description = "The location of the event.", type = FieldType.NULLABLETEXT) String location,
			@FieldTag(name = "Starts at", description = "The date and time when the event starts.", type = FieldType.TIMESTAMP, publishable = false) String starts,
			@FieldTag(name = "Ends at", description = "The date and time when the event ends.", type = FieldType.TIMESTAMP, publishable = false) String ends) {

		TimeZone timezone = TimeZone.getTimeZone(this.getUser().getTimezone());

		Event event = new Event();
		event.setSummary(title);
		if (!description.isEmpty()) {
			event.setDescription(description);
		}
		if (!location.isEmpty()) {
			event.setLocation(location);
		}

		DateFormat fromFormat = new SimpleDateFormat(Validator.TIMESTAMP_FORMAT);
		fromFormat.setTimeZone(timezone);

		Date startDate;
		try {
			startDate = fromFormat.parse(starts);
		} catch (ParseException e) {
			throw new SchedulerException();
		}
		if (startDate == null) {
			throw new SchedulerException();
		}

		DateTime startDateTime = new DateTime(startDate, timezone);
		EventDateTime startEDT = new EventDateTime().setDateTime(startDateTime)
				.setTimeZone(this.getUser().getTimezone());
		event.setStart(startEDT);

		Date endDate;
		try {
			endDate = fromFormat.parse(ends);
		} catch (ParseException e) {
			throw new SchedulerException();
		}
		if (endDate == null) {
			throw new SchedulerException();
		}

		DateTime endDateTime = new DateTime(endDate, timezone);
		EventDateTime endEDT = new EventDateTime().setDateTime(endDateTime).setTimeZone(this.getUser().getTimezone());
		event.setEnd(endEDT);

		try {
			Calendar gCalendar = this.getCalendarService();
			gCalendar.events().insert("primary", event).execute();
		} catch (GeneralSecurityException | IOException e) {
			throw new SchedulerException();
		}
	}

	/**
	 * This method returns an instance of the Calendar service.
	 */
	private Calendar getCalendarService() throws GeneralSecurityException, IOException {
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
		GoogleClientSecrets secrets = GoogleClientSecrets.load(jsonFactory, clientSecret);
		Credential credentials = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory)
				.setClientSecrets(secrets).build().setAccessToken(this.getChannelConnector().getToken())
				.setRefreshToken(this.getChannelConnector().getRefreshToken());
		return new Calendar.Builder(httpTransport, jsonFactory, credentials).setApplicationName("IFTTT-CLONE").build();
	}

	/**
	 * This method returns true if the keyword matches the event or if the
	 * keyword is empty.
	 */
	private boolean filterEvent(String keyword, Event event) {
		if (keyword.isEmpty()) {
			return true;
		}

		String title = event.getSummary();
		String description = event.getDescription();
		String location = event.getLocation();

		if (title == null) {
			title = "";
		}

		if (description == null) {
			description = "";
		}

		if (location == null) {
			location = "";
		}

		if (title.contains(keyword) || description.contains(keyword) || location.contains(keyword)) {
			return true;
		}

		return false;
	}

	private void addIngredients(Event event, Map<String, String> ingredients) {
		DateFormat toFormat = new SimpleDateFormat(Validator.TIMESTAMP_FORMAT);
		toFormat.setTimeZone(TimeZone.getTimeZone(this.getUser().getTimezone()));

		DateTime start = event.getStart().getDateTime();
		// This is not supposed to happen
		if (start == null) {
			start = event.getStart().getDate();
		}

		Date startDate;
		if (start != null) {
			startDate = new Date(start.getValue());
		} else {
			startDate = new Date();
		}

		DateTime end = event.getEnd().getDateTime();
		// This is not supposed to happen
		if (end == null) {
			end = event.getStart().getDate();
		}

		Date endDate;
		if (end != null) {
			endDate = new Date(end.getValue());
		} else {
			endDate = new Date();
		}

		String title = event.getSummary();
		if (title == null) {
			title = "";
		}

		String description = event.getDescription();
		if (description == null) {
			description = "";
		}

		String location = event.getLocation();
		if (location == null) {
			location = "";
		}

		ingredients.put("Starts", toFormat.format(startDate));
		ingredients.put("Ends", toFormat.format(endDate));
		ingredients.put("Title", title);
		ingredients.put("Description", description);
		ingredients.put("Where", location);
	}

}
