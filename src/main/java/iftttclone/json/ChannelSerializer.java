package iftttclone.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import iftttclone.entities.Channel;

public class ChannelSerializer extends JsonSerializer<Channel> {

	@Override
	public void serialize(Channel value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {

		if (value == null) {
			gen.writeNull();
		} else {
			gen.writeString(value.getId());
		}

	}

}
