package iftttclone.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import iftttclone.entities.Action;

public class ActionSerializer extends JsonSerializer<Action> {

	@Override
	public void serialize(Action action, JsonGenerator gen, SerializerProvider prov)
			throws IOException, JsonProcessingException {
		if (action == null) {
			gen.writeNull();
		} else {
			gen.writeStartObject();
			gen.writeStringField("channel", action.getChannel().getId());
			gen.writeStringField("method", action.getMethod());
			gen.writeEndObject();
		}
	}

}
