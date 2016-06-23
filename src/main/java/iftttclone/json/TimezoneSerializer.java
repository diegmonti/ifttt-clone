package iftttclone.json;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import iftttclone.core.TimezoneManager;

public class TimezoneSerializer extends JsonSerializer<String> {
	@Autowired
	private TimezoneManager timezoneManager;

	@Override
	public void serialize(String value, JsonGenerator gen, SerializerProvider serializers)
			throws IOException, JsonProcessingException {

		if (value == null) {
			gen.writeNull();
		} else {
			gen.writeString(timezoneManager.getNameFromId(value));
		}

	}

}
