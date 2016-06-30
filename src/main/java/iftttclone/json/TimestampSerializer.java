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

import iftttclone.core.Utils;
import iftttclone.entities.User;

/**
 * The time stamp, saved in the database as a numerical value, is converted in a
 * string according to the time zone of the user.
 */
public class TimestampSerializer extends JsonSerializer<Long> {
	@Autowired
	private Utils utils;

	@Override
	public void serialize(Long value, JsonGenerator gen, SerializerProvider prov)
			throws IOException, JsonProcessingException {
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
		User user = utils.getCurrentUser();

		if (user != null) {
			formatter.setTimeZone(TimeZone.getTimeZone(user.getTimezone()));
		}

		if (value == null) {
			gen.writeNull();
		} else {
			gen.writeString(formatter.format(new Date(value)));
		}

	}
}