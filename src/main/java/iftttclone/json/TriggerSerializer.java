package iftttclone.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import iftttclone.entities.Trigger;

public class TriggerSerializer extends JsonSerializer<Trigger> {

	@Override
	public void serialize(Trigger trigger, JsonGenerator gen, SerializerProvider prov)
			throws IOException, JsonProcessingException {
		if (trigger == null) {
			gen.writeNull();
		} else {
			gen.writeStartObject();
			gen.writeStringField("channel", trigger.getChannel().getId());
			gen.writeStringField("method", trigger.getMethod());
			gen.writeEndObject();
		}
	}

}
