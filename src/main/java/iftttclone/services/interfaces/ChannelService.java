package iftttclone.services.interfaces;

import java.util.Collection;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;
import iftttclone.entities.Trigger;

public interface ChannelService {

	public Collection<Channel> getChannels();

	public Channel getChannel(String channelName);

	public Collection<Trigger> getChannelTriggers(String channelName);

	public Collection<Action> getChannelActions(String channelName);

}
