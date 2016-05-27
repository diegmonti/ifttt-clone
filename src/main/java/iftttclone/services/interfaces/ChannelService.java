package iftttclone.services.interfaces;

import java.util.Collection;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;
import iftttclone.entities.Trigger;

public interface ChannelService {

	public Collection<Channel> getChannels();

	public Channel getChannel(Long channelId);

	public Collection<Trigger> getChannelTriggers(Long channelId);

	public Collection<Action> getChannelActions(Long channelId);

}
