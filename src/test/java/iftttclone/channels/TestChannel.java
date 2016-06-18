package iftttclone.channels;

import java.util.HashMap;
import java.util.Map;

import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.FieldTag;
import iftttclone.channels.annotations.TriggerTag;

@ChannelTag(name = "Test Channel", description = "A fake channel for testing", withConnection = false)
public class TestChannel extends AbstractChannel {

	@TriggerTag(name = "Simple trigger", description = "Example")
	@IngredientTag(name = "Key1", description = "", example = "")
	@IngredientTag(name = "Key2", description = "", example = "")
	public Map<String, String> simpleTrigger(@FieldTag(name = "Value", description = "Example", publishable = true) String value,
			@FieldTag(name = "Run", description = "Example", publishable = false) String run) {
		System.err.println("This is the simple trigger");
		System.err.println("The value is: " + value);

		if (!run.equals("yes"))
			return null;

		Map<String, String> result = new HashMap<String, String>();
		result.put("Key1", "value1");
		result.put("Key2", "value2");
		return result;
	}

	@ActionTag(name = "Simple action", description = "Example")
	public void simpleAction(@FieldTag(name = "Value", description = "Example", publishable = true) String value) {
		System.err.println("This is the simple action");
		System.err.println("The value is: " + value);
	}

}
