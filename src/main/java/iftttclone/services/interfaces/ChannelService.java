package iftttclone.services.interfaces;

import java.util.Collection;
import java.util.List;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;
import iftttclone.entities.Trigger;

public interface ChannelService {

	public List<Channel> getChannels();
	public Channel getChannel(Long channelId);
	public Collection<Trigger> getChannelTriggers(Long channelId);
	public Collection<Action> getChannelActions(Long channelId);
}
