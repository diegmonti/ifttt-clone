package iftttclone.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;
import iftttclone.entities.Trigger;
import iftttclone.repositories.ActionRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.TriggerRepository;
import iftttclone.services.interfaces.ChannelService;

public class ChannelServiceImpl implements ChannelService {
	@Autowired
	private ChannelRepository channels;
	@Autowired
	private TriggerRepository triggers;
	@Autowired
	private ActionRepository actions;

	@Override
	@Transactional
	public Collection<Channel> getChannels() {
		return channels.findAll();
	}

	@Override
	@Transactional
	public Channel getChannel(Long channelId) {
		Channel channel = channels.findOne(channelId);
		if (channel == null) {
			// TODO throw new Exception();
		}
		return channel;
	}

	@Override
	@Transactional
	public Collection<Trigger> getChannelTriggers(Long channelId) {
		Channel channel = channels.findOne(channelId);
		if (channel == null) {
			// TODO throw new Exception();
		}
		return triggers.getTriggersByChannel(channel);
	}

	@Override
	@Transactional
	public Collection<Action> getChannelActions(Long channelId) {
		Channel channel = channels.findOne(channelId);
		if (channel == null) {
			// TODO throw new Exception();
		}
		return actions.getActionsByChannel(channel);
	}

}
