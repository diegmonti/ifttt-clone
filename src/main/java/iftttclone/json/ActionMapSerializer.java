package iftttclone.json;

import java.io.IOException;
import java.util.Map;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import iftttclone.entities.Action;

/**
 * If it is part of a summary, only true or false is returned. This indicates
 * the presence or the absence of at least an action.
 */
public class ActionMapSerializer extends JsonSerializer<Map<String, Action>> {

	@Override
	public void serialize(Map<String, Action> value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {

		if (value == null) {
			gen.writeNull();
			return;
		}

		if (serializers.getActiveView() != null) {
			gen.writeBoolean(value.isEmpty() ? false : true);
		} else {
			gen.writeObject(value);
		}

	}
}
