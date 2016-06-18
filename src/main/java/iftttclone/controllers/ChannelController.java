package iftttclone.controllers;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonView;

import iftttclone.entities.Channel;
import iftttclone.json.JsonViews;
import iftttclone.services.interfaces.ChannelService;

@RestController
@RequestMapping("/channels")
public class ChannelController {
	@Autowired
	ChannelService channelService;

	@JsonView(JsonViews.Summary.class)
	@RequestMapping(value = "", method = RequestMethod.GET)
	public Collection<Channel> getChannels() {
		return channelService.getChannels();
	}

	@RequestMapping(value = "/{channelId}", method = RequestMethod.GET)
	public Channel getChannel(@PathVariable String channelId) {
		return channelService.getChannel(channelId);
	}

}
