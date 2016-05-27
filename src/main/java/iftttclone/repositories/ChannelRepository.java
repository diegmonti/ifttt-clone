package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Channel;

public interface ChannelRepository extends Repository<Channel, Long> {

	Channel getChannelByClasspath(String classpath);

	Channel save(Channel channel);

}
