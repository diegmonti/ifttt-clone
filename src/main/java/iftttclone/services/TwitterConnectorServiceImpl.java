package iftttclone.services;


//import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.env.Environment;
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
//import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.TwitterConnectorService;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

@Component
public class TwitterConnectorServiceImpl implements TwitterConnectorService {
	//private final static String callback = "/api/channels/twitter/authorize";
	/*@Autowired
	private Environment env;*/
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private ChannelConnectorRepository channelConnectorRepository;
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private Utils utils;
	/*@Autowired
	private UserRepository userRepository;*/

	private final static String channelPath = TwitterChannel.class.getName();
	//Twitter twitter;
	
	/*@PostConstruct
	public void init(){
		twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer(env.getProperty("twitter.consumer.key"), env.getProperty("twitter.consumer.secret"));
	}*/
	/*public TwitterConnectorServiceImpl(){
		twitter = TwitterFactory.getSingleton();
	    twitter.setOAuthConsumer(env.getProperty("twitter.consumer.key"), env.getProperty("twitter.consumer.secret"));
	}*/

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

		//Twitter twitter = TwitterFactory.getSingleton();
		Twitter twitter = new TwitterFactory().getInstance();
	    //twitter.setOAuthConsumer(env.getProperty("twitter.consumer.key"), env.getProperty("twitter.consumer.secret"));
	    RequestToken requestToken;
		try {
			requestToken = twitter.getOAuthRequestToken();
		} catch (TwitterException e) {	// should not happen
			// TODO Auto-generated catch block
			e.printStackTrace();
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
		//Twitter twitter = TwitterFactory.getSingleton();
		//twitter.setOAuthConsumer(env.getProperty("twitter.consumer.key"), env.getProperty("twitter.consumer.secret"));

		AccessToken aToken;
		try {
			aToken = twitter.getOAuthAccessToken(new RequestToken(channelConnector.getToken(), channelConnector.getRefreshToken())
					, code);
			/*// get first twitter to check if exists
			if(!twitter.getUserTimeline().isEmpty()){
				/*user.setSinceId(twitter.getUserTimeline().get(0).getId());
				userRepository.save(user);*
				recipeRepository.setAll_SinceId_ByUserAndChannel(user, channel, twitter.getUserTimeline().get(0).getId());
			}*/
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return;
		}

		channelConnector.setToken(aToken.getToken());
		channelConnector.setRefreshToken(aToken.getTokenSecret());
		channelConnector.setConnectionTime(System.currentTimeMillis());

		channelConnectorRepository.save(channelConnector);

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
		// NO: since_id is left since it is not used outside twitter
		//recipeRepository.unsetSinceIdByUserAndTriggerChannel(user, channel);	// reset last execution time
	}

}
