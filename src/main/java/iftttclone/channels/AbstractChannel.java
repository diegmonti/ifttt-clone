package iftttclone.channels;

import java.util.Date;

import iftttclone.entities.ChannelConnector;
import iftttclone.entities.User;

/**
 * This class contains the information needed by the channels to execute triggers and actions.
 */
public abstract class AbstractChannel {
	/**
	 * The information needed to interact with an external service on behalf of
	 * the current user.
	 */
	private ChannelConnector channelConnector;
	/**
	 * The last time the recipe triggered an action.
	 */
	private Date lastRun;
	/**
	 * The current user, useful to get the time zone.
	 */
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
