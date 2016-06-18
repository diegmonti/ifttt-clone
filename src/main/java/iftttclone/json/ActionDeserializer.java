package iftttclone.json;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;
import iftttclone.repositories.ActionRepository;
import iftttclone.repositories.ChannelRepository;

public class ActionDeserializer extends JsonDeserializer<Action> {
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private ActionRepository actionRepository;

	@Override
	public Action deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectCodec oc = p.getCodec();
		JsonNode node = oc.readTree(p);
		Channel channel = channelRepository.findOne(node.get("channel").asText());
		if (channel == null) {
			return null;
		}
		String method = node.get("method").asText();
		return actionRepository.getActionByMethodAndChannel(method, channel);
	}

}
