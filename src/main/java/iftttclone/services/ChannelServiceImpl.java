package iftttclone.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;
import iftttclone.entities.ChannelConnector;
import iftttclone.entities.Trigger;
import iftttclone.entities.User;
import iftttclone.exceptions.ResourceNotFoundException;
import iftttclone.repositories.ActionRepository;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.TriggerRepository;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.ChannelService;

@Component
@Transactional
public class ChannelServiceImpl implements ChannelService {
	@Autowired
	private ChannelRepository channels;
	@Autowired
	private TriggerRepository triggers;
	@Autowired
	private ActionRepository actions;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChannelConnectorRepository channelConnectorRepository;

	@Override
	public Collection<Channel> getChannels() {
		Collection<Channel> collection = channels.findAll();

		// Get the current user, if exists
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth.isAuthenticated()) {
			User user = userRepository.getUserByUsername(auth.getName());
			for (Channel channel : collection) {
				ChannelConnector channelConnector = channelConnectorRepository
						.getChannelConnectorByChannelAndUser(channel, user);
				if (channelConnector != null) {
					if (channelConnector.getConnectionTime() != null)
						channel.setConnected(true);
				}
			}
		}

		return collection;
	}

	@Override
	public Channel getChannel(String channelId) {
		Channel channel = channels.findOne(channelId);

		if (channel == null) {
			throw new ResourceNotFoundException();
		}

		// Get the current user, if exists
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if (auth.isAuthenticated()) {
			User user = userRepository.getUserByUsername(auth.getName());
			ChannelConnector channelConnector = channelConnectorRepository.getChannelConnectorByChannelAndUser(channel,
					user);
			if (channelConnector != null) {
				if (channelConnector.getConnectionTime() != null)
					channel.setConnected(true);
			}
		}

		return channel;
	}

	@Override
	public Collection<Trigger> getChannelTriggers(String channelId) {
		Channel channel = channels.findOne(channelId);

		if (channel == null) {
			throw new ResourceNotFoundException();
		}

		return triggers.getTriggersByChannel(channel);
	}

	@Override
	public Collection<Action> getChannelActions(String channelId) {
		Channel channel = channels.findOne(channelId);

		if (channel == null) {
			throw new ResourceNotFoundException();
		}

		return actions.getActionsByChannel(channel);
	}

}
