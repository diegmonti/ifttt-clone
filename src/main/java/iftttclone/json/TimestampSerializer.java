package iftttclone.json;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import iftttclone.services.interfaces.UserService;

public class TimestampSerializer extends JsonSerializer<Long> {
	@Autowired
	private UserService userService;

	@Override
	public void serialize(Long value, JsonGenerator gen, SerializerProvider prov)
			throws IOException, JsonProcessingException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		String timezone = userService.getUser().getTimezone();
		formatter.setTimeZone(TimeZone.getTimeZone(timezone));

		if (value == null) {
			gen.writeNull();
		} else {
			gen.writeString(formatter.format(new Date(value)));
		}

	}
}