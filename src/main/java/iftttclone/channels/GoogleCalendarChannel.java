package iftttclone.channels;

import java.util.HashMap;
import java.util.Map;

import iftttclone.channels.annotations.ActionFieldTag;
import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.TriggerFieldTag;
import iftttclone.channels.annotations.TriggerTag;

@ChannelTag(name = "Google Calendar", description = "The channel for google calendar", withConnection = true)
public class GoogleCalendarChannel extends AbstractChannel {

	@TriggerTag(name = "EventStarted", description = "This trigger fires when an event starts, it can be filtered with some keywords. Left blank if not needed")
	@IngredientTag(name = "When", description = "When the event started", example = "23/05/2016 13:09")
	@IngredientTag(name = "Title", description = "The title of the event", example = "Important exam")
	@IngredientTag(name = "Description", description = "The description of the event", example = "That course I hate")
	@IngredientTag(name = "Where", description = "The location of the event", example = "10I")
	public Map<String, String> newEventStarted(
			@TriggerFieldTag(name = "titleKeyword", description = "A keyword to search in the title of the event", isPublishable = true) String titleKW,
			@TriggerFieldTag(name = "DescriptionKeyword", description = "A keyword to search in the description of the event", isPublishable = true) String descKW,
			@TriggerFieldTag(name = "locationKeyword", description = "A keyword to search in the location of the event", isPublishable = false) String locationKW) {

		// TODO: do something

		Map<String, String> ingredients = new HashMap<String, String>();
		return ingredients;
	}

	@TriggerTag(name = "EventAdded", description = "This trigger fires when an event is added, it can be filtered with some keywords. Left blank if not needed")
	@IngredientTag(name = "WhenStarts", description = "When the event starts", example = "23/05/2016 13:09")
	@IngredientTag(name = "WhenEnds", description = "When the event ends", example = "23/05/2016 15:09")
	@IngredientTag(name = "Title", description = "The title of the event", example = "Important exam")
	@IngredientTag(name = "Description", description = "The description of the event", example = "That course I hate")
	@IngredientTag(name = "Where", description = "The location of the event", example = "10I")
	public Map<String, String> newEventAdded(
			@TriggerFieldTag(name = "titleKeyword", description = "A keyword to search in the title of the event", isPublishable = true) String titleKW,
			@TriggerFieldTag(name = "DescriptionKeyword", description = "A keyword to search in the description of the event", isPublishable = true) String descKW,
			@TriggerFieldTag(name = "locationKeyword", description = "A keyword to search in the location of the event", isPublishable = false) String locationKW) {

		// TODO: do something

		Map<String, String> ingredients = new HashMap<String, String>();
		return ingredients;
	}

	@ActionTag(name = "CreateEvent", description = "Creates a new Event")
	public void createEvent(
			@ActionFieldTag(name = "Title", description = "The title of the event", isPublishable = true) String title,
			@ActionFieldTag(name = "Description", description = "The description of the event", isPublishable = true) String description,
			@ActionFieldTag(name = "Where", description = "The location of the event", isPublishable = false) String location,
			@ActionFieldTag(name = "WhenStarts", description = "When the event starts", isPublishable = false) String starts) {

		// TODO: do something

	}

}
