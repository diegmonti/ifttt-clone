package iftttclone.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import iftttclone.channels.TwitterChannel;
import iftttclone.core.Utils;
import iftttclone.entities.Channel;
import iftttclone.entities.ChannelConnector;
import iftttclone.entities.User;
import iftttclone.exceptions.ForbiddenException;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.RecipeRepository;
import iftttclone.services.interfaces.TwitterConnectorService;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@Component
public class TwitterConnectorServiceImpl implements TwitterConnectorService {
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private ChannelConnectorRepository channelConnectorRepository;
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private Utils utils;

	private final static String channelPath = TwitterChannel.class.getName();

	@Override
	public String requestConnection(String path) {
		// Get the current user
		User user = utils.getCurrentUser();

		// Get channel
		Channel channel = channelRepository.getChannelByClasspath(channelPath);

		// Get channel connector
		ChannelConnector channelConnector = channelConnectorRepository.getChannelConnectorByChannelAndUser(channel,
				user);

		if (channelConnector == null) {
			channelConnector = new ChannelConnector();
			channelConnector.setChannel(channel);
			channelConnector.setUser(user);
		}

		// Because we are trying to create a new connection
		channelConnector.setConnectionTime(null);

		Twitter twitter = new TwitterFactory().getInstance();
		RequestToken requestToken;
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {
			// Should not happen
			return null;
		}
		String url = requestToken.getAuthorizationURL();

		channelConnector.setToken(requestToken.getToken());
		channelConnector.setRefreshToken(requestToken.getTokenSecret());

		channelConnectorRepository.save(channelConnector);

		return url;
	}

	@Override
	public void validateConnection(String path, String code, String token) {
		// Get the current user
		User user = utils.getCurrentUser();

		// Get channel
		Channel channel = channelRepository.getChannelByClasspath(channelPath);

		// Get channel connector
		ChannelConnector channelConnector = channelConnectorRepository.getChannelConnectorByChannelAndUser(channel,
				user);

		if (channelConnector == null) {
			throw new ForbiddenException();
		}

		if (!channelConnector.getToken().equals(token)) {
			throw new ForbiddenException();
		}

		Twitter twitter = new TwitterFactory().getInstance();

		AccessToken aToken;
		try {
			aToken = twitter.getOAuthAccessToken(
					new RequestToken(channelConnector.getToken(), channelConnector.getRefreshToken()), code);
			
			twitter.setOAuthAccessToken(aToken);
			
			channelConnector.setToken(aToken.getToken());
			channelConnector.setRefreshToken(aToken.getTokenSecret());
			channelConnector.setConnectionTime(System.currentTimeMillis());
			channelConnector.setAccount(twitter.getScreenName());

			channelConnectorRepository.save(channelConnector);
		} catch (TwitterException e) {
			
		}

	}

	@Override
	public void removeConnection() {
		// Get the current user
		User user = utils.getCurrentUser();

		// Get channel
		Channel channel = channelRepository.getChannelByClasspath(channelPath);

		// Get channel connector
		ChannelConnector channelConnector = channelConnectorRepository.getChannelConnectorByChannelAndUser(channel,
				user);

		channelConnectorRepository.delete(channelConnector);
		recipeRepository.setAllInactiveByUserAndChannel(user, channel);
	}

}
