package iftttclone.channels;

import java.util.Date;

import iftttclone.entities.ChannelConnector;
import iftttclone.entities.User;

public abstract class AbstractChannel {
	private ChannelConnector channelConnector;
	private Date lastRun;
	private User user;

	public ChannelConnector getChannelConnector() {
		return channelConnector;
	}

	public void setChannelConnector(ChannelConnector channelConnector) {
		this.channelConnector = channelConnector;
	}

	public Date getLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

}
