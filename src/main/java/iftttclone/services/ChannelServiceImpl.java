package iftttclone.services;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.Channel;
import iftttclone.entities.ChannelConnector;
import iftttclone.entities.User;
import iftttclone.exceptions.ResourceNotFoundException;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.ChannelService;

@Component
@Transactional
public class ChannelServiceImpl implements ChannelService {
	@Autowired
	private ChannelRepository channels;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChannelConnectorRepository channelConnectorRepository;

	@Override
	public Collection<Channel> getChannels() {
		Collection<Channel> collection = channels.findAll();

		// Get the current user, if exists
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		// Set connection time, if present
		if (auth.isAuthenticated()) {
			User user = userRepository.getUserByUsername(auth.getName());
			for (Channel channel : collection) {
				setConnectionTime(channel, user);
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

		// Set connection time, if present
		if (auth.isAuthenticated()) {
			User user = userRepository.getUserByUsername(auth.getName());
			setConnectionTime(channel, user);
		}

		return channel;
	}

	private void setConnectionTime(Channel channel, User user) {
		ChannelConnector channelConnector = channelConnectorRepository.getChannelConnectorByChannelAndUser(channel,
				user);
		if (channelConnector != null) {
			if (channelConnector.getConnectionTime() != null) {
				channel.setConnectionTime(channelConnector.getConnectionTime());
			}
		}
	}

}
