package iftttclone.services.interfaces;

import java.util.Collection;

import iftttclone.entities.Channel;

public interface ChannelService {

	public Collection<Channel> getChannels();

	public Channel getChannel(String channelName);

}
