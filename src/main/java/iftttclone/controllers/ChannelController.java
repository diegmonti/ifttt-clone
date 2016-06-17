package iftttclone.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;
import iftttclone.entities.Trigger;
import iftttclone.entities.utils.View;
import iftttclone.services.interfaces.ChannelService;

@RestController
@RequestMapping("/channels")
public class ChannelController {
	@Autowired
	ChannelService channelService;

	@JsonView(View.Summary.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Collection<Channel> getChannels() {
		return channelService.getChannels();
	}

	@RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
	public Channel getChannel(@PathVariable String channelId) {
		return channelService.getChannel(channelId);
	}

	@RequestMapping(value = "/{channelId}/triggers", method = RequestMethod.GET)
	public Collection<Trigger> getChannelTriggers(@PathVariable String channelId) {
		return channelService.getChannelTriggers(channelId);
	}

	@RequestMapping(value = "/{channelId}/actions", method = RequestMethod.GET)
	public Collection<Action> getChannelActions(@PathVariable String channelId) {
		return channelService.getChannelActions(channelId);
	}

}
