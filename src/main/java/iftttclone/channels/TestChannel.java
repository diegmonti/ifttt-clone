package iftttclone.channels;

import java.util.HashMap;
import java.util.Map;

import iftttclone.channels.annotation.ActionTag;
import iftttclone.channels.annotation.ActionFieldTag;
import iftttclone.channels.annotation.ChannelTag;
import iftttclone.channels.annotation.IngredientTag;
import iftttclone.channels.annotation.TriggerTag;
import iftttclone.channels.annotation.TriggerFieldTag;

@ChannelTag(name = "Test channel", description = "A fake channel for testing")
public class TestChannel extends AbstractChannel {

	@TriggerTag(name = "Simple trigger", description = "Example")
	@IngredientTag(name = "Key1", description = "", example = "")
	@IngredientTag(name = "Key2", description = "", example = "")
	public Map<String, String> simpleTrigger(@TriggerFieldTag(name = "Value", description = "Example") String value,
			@TriggerFieldTag(name = "Run", description = "Example") String run) {
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
	public void simpleAction(@ActionFieldTag(name = "Value", description = "Example") String value) {
		System.err.println("This is the simple action");
		System.err.println("The value is: " + value);
	}

}
