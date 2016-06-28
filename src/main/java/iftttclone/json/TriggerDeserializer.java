package iftttclone.json;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import iftttclone.entities.Channel;
import iftttclone.entities.Trigger;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.TriggerRepository;

/**
 * The client identifies a trigger with the pair channel name and method name.
 */
public class TriggerDeserializer extends JsonDeserializer<Trigger> {
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private TriggerRepository triggerRepository;

	@Override
	public Trigger deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
		ObjectCodec oc = p.getCodec();
		JsonNode node = oc.readTree(p);
		Channel channel = channelRepository.findOne(node.get("channel").asText());
		if (channel == null) {
			return null;
		}
		String method = node.get("method").asText();
		return triggerRepository.getTriggerByMethodAndChannel(method, channel);
	}

}
