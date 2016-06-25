package iftttclone.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import iftttclone.entities.Trigger;

public class TriggerSerializer extends JsonSerializer<Trigger> {

	@Override
	public void serialize(Trigger value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {

		if (value == null) {
			gen.writeNull();
			return;
		}

		if (serializers.getActiveView() != null) {
			gen.writeStartObject();
			gen.writeStringField("channel", value.getChannel().getId());
			gen.writeEndObject();
		} else {
			gen.writeObject(value);
		}
	}

}
