package iftttclone.channels;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.FieldTag;
import iftttclone.channels.annotations.TriggerTag;
import iftttclone.core.Validator.FieldType;

@ChannelTag(name = "Test Channel", description = "A fake channel for testing", withConnection = false)
public class TestChannel extends AbstractChannel {

	@TriggerTag(name = "Simple trigger", description = "Example")
	@IngredientTag(name = "Key1", description = "", example = "")
	@IngredientTag(name = "Key2", description = "", example = "")
	@IngredientTag(name = "Key", description = "", example = "")
	public List<Map<String, String>> simpleTrigger(
			@FieldTag(name = "Value", description = "Example", type = FieldType.TEXT) String value,
			@FieldTag(name = "Run", description = "Example", type = FieldType.TEXT, publishable = false) String run) {
		
		System.err.println("This is the simple trigger");
		System.err.println("The value is: " + value);

		if (!run.equals("yes"))
			return null;

		Map<String, String> resEntry = new HashMap<String, String>();
		resEntry.put("Key1", "value1");
		resEntry.put("Key2", "value2");
		resEntry.put("Key", value);
		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		result.add(resEntry);
		return result;
	}

	@ActionTag(name = "Simple action", description = "Example")
	public void simpleAction(
			@FieldTag(name = "Value", description = "Example", type = FieldType.TEXT) String value) {
		
		System.err.println("This is the simple action");
		System.err.println("The value is: " + value);
	}

}
