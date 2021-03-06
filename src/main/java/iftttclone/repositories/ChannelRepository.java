package iftttclone.repositories;

import java.util.Collection;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Channel;

public interface ChannelRepository extends Repository<Channel, String> {

	Collection<Channel> findAll();

	Channel findOne(String channelId);

	Channel getChannelByClasspath(String classpath);

	Channel save(Channel channel);

}
