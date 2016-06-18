package iftttclone.json;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import iftttclone.entities.User;

public class UserSerializer extends JsonSerializer<User> {

	@Override
	public void serialize(User user, JsonGenerator gen, SerializerProvider prov)
			throws IOException, JsonProcessingException {
		if (user == null) {
			gen.writeNull();
		} else {
			gen.writeString(user.getUsername());
		}
	}

}
