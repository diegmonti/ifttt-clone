package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Channel;
import iftttclone.entities.ChannelConnector;
import iftttclone.entities.User;

public interface ChannelConnectorRepository extends Repository<ChannelConnector, Long> {

	ChannelConnector getChannelConnectorByChannelAndUser(Channel channel, User user);
	
	ChannelConnector save(ChannelConnector channelConnector);
	
	void delete(ChannelConnector channelConnector);
}
