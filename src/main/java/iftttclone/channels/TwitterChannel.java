package iftttclone.channels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

//import org.aspectj.lang.annotation.Aspect;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.beans.factory.annotation.Configurable;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.core.env.Environment;
//import org.springframework.transaction.annotation.Transactional;

import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.FieldTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.TriggerTag;
import iftttclone.core.Validator;
import iftttclone.core.Validator.FieldType;
//import iftttclone.entities.Recipe;
//import iftttclone.repositories.RecipeRepository;
//import iftttclone.repositories.UserRepository;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

//@Aspect
//@Configurable//(preConstruction = true)
// the twitter api makes some restriction about the maxinum number of tweets to retrieve (newest 3200), as well as the number of read calls in a window (15 in 15 minutes per user), write calls limits are shared for all applications in a per user basis
@ChannelTag(name = "Twitter", description = "Twitter is a free online social networking service that enables users to send and read short 140-character messages called \"tweets\"", withConnection = true)
public class TwitterChannel extends AbstractChannel {
	/*@Autowired
	private UserRepository userRepository;*/
	/*@Autowired
	private RecipeRepository recipeRepository;*/
	/*@Autowired
	private Environment env;*/
	/*@Value("${twitter.consumer.key}")
	private String consumerKey;
	@Value("${twitter.consumer.secret}")
	private String consumerSecret;*/

	//@Transactional	// this is to make sure the write is done even if something else fails in the scheduler
	@TriggerTag(name = "New tweet by you", description = "This trigger fires when you post a new tweet.")
	@IngredientTag(name = "Text", description = "The actual tweet.", example = "I am eating something")
	@IngredientTag(name = "UserName", description = "User name of tweeter.", example = "twitterMan")
	@IngredientTag(name = "CreatedAt", description = "The date and time of the reception of the tweet.", example = "23/05/2016 13:09")
	//@IngredientTag(name = "LinkToTweet", description = "The URL of the tweet.", example = "http://twitter.com/ifttt/status/99999999999999999")
	public List<Map<String, String>> newTweetByYou() {

		/*Twitter twitter = this.getTwitterService();
		
		List<Map<String, String>> result = new LinkedList<Map<String, String>>();*/
		/*// the code could (and should) be changed using getUserTimeline(new Paging(sinceId)) 
		// but for this sinceId must be saved between calls and when connecting (first time)
		boolean loadMore = true;
		while(loadMore){
			List<Status> statuses = twitter.getUserTimeline();
			for (Status status : statuses) {
				if (this.getLastRun().before(createdAt)) {
					loadMore = false;
					break;
				}
				
				Map<String, String> resEntry = new HashMap<String, String>();
				this.addIngredients(message, resEntry);
				result.add(resEntry);
			}
		}*/
		
		//Long sinceId = this.getUser().getSinceId();
		//Recipe recipe = recipeRepository.findRecipeByIdAndUser(this.getRecipeId(), this.getUser());
		/*Recipe recipe = this.getRecipeRepository().findRecipeByIdAndUser(this.getRecipeId(), this.getUser());
		Long sinceId = recipe.getSinceId();
		Long newSinceId = null;
		long maxId;
		Paging paging = new Paging();
		// TODO check if newSinceId and maxId are correct or must keep track of them (tests)
		if(sinceId != null){
			newSinceId = sinceId;
			maxId = sinceId+1;
			try {
				paging.setSinceId(sinceId);
				//List<Status> statuses = twitter.getUserTimeline(new Paging(sinceId));
				List<Status> statuses = twitter.getUserTimeline(paging);
				if(!statuses.isEmpty()){	// the first tweet of the first call is the newest
					newSinceId = statuses.get(0).getId();
				}
				while(!statuses.isEmpty()){	//while(maxId>sinceId){
					for (Status status : statuses) {
						Map<String, String> resEntry = new HashMap<String, String>();
						this.addIngredients(status, resEntry);
						result.add(resEntry);
						
						maxId = status.getId();
					}
					maxId--;
					if(maxId < 0){
						break;
					}
					paging.maxId(maxId);
					statuses = twitter.getUserTimeline(paging);
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {	// first time executing or after some cancellation by the user
			maxId = 0;
			boolean loadMore = true;
			try {
				List<Status> statuses = twitter.getUserTimeline();
				if(!statuses.isEmpty()){	// the first tweet of the first call is the newest
					newSinceId = statuses.get(0).getId();
				}
				while(loadMore && !statuses.isEmpty()){
					for (Status status : statuses) {
						Date createdAt = status.getCreatedAt();
						if (this.getLastRun().before(createdAt)) {
							loadMore = false;
							break;
						}
						
						Map<String, String> resEntry = new HashMap<String, String>();
						this.addIngredients(status, resEntry);
						result.add(resEntry);
						
						maxId = status.getId();
					}
					maxId--;
					if(maxId < 0){
						break;
					}
					paging.maxId(maxId);
					statuses = twitter.getUserTimeline(paging);
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}*/
		/*else {	// there were no tweets when the channel was connected
			maxId = 0;
			try {
				List<Status> statuses = twitter.getUserTimeline();
				if(!statuses.isEmpty()){	// the first tweet of the first call is the newest
					newSinceId = statuses.get(0).getId();
				}
				while(!statuses.isEmpty()){
					for (Status status : statuses) {
						Map<String, String> resEntry = new HashMap<String, String>();
						this.addIngredients(status, resEntry);
						result.add(resEntry);
						
						maxId = status.getId();
					}
					maxId--;
					paging.maxId(maxId);
					statuses = twitter.getUserTimeline(paging);
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}*/
		/*this.getUser().setSinceId(newSinceId);
		userRepository.save(this.getUser());*/
		//recipeRepository.setSinceId(this.getRecipeId(), newSinceId);
		/*this.getRecipeRepository().setSinceId(this.getRecipeId(), newSinceId);

		return result;*/
		
		
		Twitter twitter = this.getTwitterService();
		
		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		
		long maxId = 0;
		Paging paging = new Paging();
		boolean loadMore = true;
		try {
			List<Status> statuses = twitter.getUserTimeline();
			while(loadMore && !statuses.isEmpty()){
				for (Status status : statuses) {
					Date createdAt = status.getCreatedAt();
					if (this.getLastRun().after(createdAt)) {
						loadMore = false;
						break;
					}
					
					Map<String, String> resEntry = new HashMap<String, String>();
					this.addIngredients(status, resEntry);
					result.add(resEntry);
					
					maxId = status.getId();
				}
				maxId--;
				if(maxId < 0){
					break;
				}
				paging.setMaxId(maxId);
				statuses = twitter.getUserTimeline(paging);
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}

		return result;
	}
	
	
	//@Transactional	// this is to make sure the write is done even if something else fails in the scheduler
	@TriggerTag(name = "New mention of you", description = "This Trigger fires every time you are @mentioned in a tweet.")
	@IngredientTag(name = "Text", description = "The actual tweet.", example = "I am eating something")
	@IngredientTag(name = "UserName", description = "User name of tweeter.", example = "twitterMan")
	@IngredientTag(name = "CreatedAt", description = "The date and time of the reception of the tweet.", example = "23/05/2016 13:09")
	//@IngredientTag(name = "LinkToTweet", description = "The URL of the tweet.", example = "http://twitter.com/ifttt/status/99999999999999999")
	public List<Map<String, String>> newMentionOfYou(){
		
		/*Twitter twitter = this.getTwitterService();
		
		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		
		//Recipe recipe = recipeRepository.findRecipeByIdAndUser(this.getRecipeId(), this.getUser());
		Recipe recipe = this.getRecipeRepository().findRecipeByIdAndUser(this.getRecipeId(), this.getUser());
		Long sinceId = recipe.getSinceId();
		Long newSinceId = null;
		long maxId;
		Paging paging = new Paging();
		if(sinceId != null){
			newSinceId = sinceId;
			maxId = sinceId+1;
			try {
				paging.setSinceId(sinceId);
				List<Status> statuses = twitter.getMentionsTimeline(paging);
				if(!statuses.isEmpty()){	// the first tweet of the first call is the newest
					newSinceId = statuses.get(0).getId();
				}
				while(!statuses.isEmpty()){
					for (Status status : statuses) {
						Map<String, String> resEntry = new HashMap<String, String>();
						this.addIngredients(status, resEntry);
						result.add(resEntry);
						
						maxId = status.getId();
					}
					maxId--;
					if(maxId < 0){
						break;
					}
					paging.maxId(maxId);
					statuses = twitter.getMentionsTimeline(paging);
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {	// first time executing or after some cancellation by the user
			maxId = 0;
			boolean loadMore = true;
			try {
				List<Status> statuses = twitter.getMentionsTimeline();
				if(!statuses.isEmpty()){	// the first tweet of the first call is the newest
					newSinceId = statuses.get(0).getId();
				}
				while(loadMore && !statuses.isEmpty()){
					for (Status status : statuses) {
						Date createdAt = status.getCreatedAt();
						if (this.getLastRun().before(createdAt)) {
							loadMore = false;
							break;
						}
						
						Map<String, String> resEntry = new HashMap<String, String>();
						this.addIngredients(status, resEntry);
						result.add(resEntry);
						
						maxId = status.getId();
					}
					maxId--;
					if(maxId < 0){
						break;
					}
					paging.maxId(maxId);
					statuses = twitter.getMentionsTimeline(paging);
				}
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		}
		//recipeRepository.setSinceId(this.getRecipeId(), newSinceId);
		this.getRecipeRepository().setSinceId(this.getRecipeId(), newSinceId);

		return result;*/
		
		
		Twitter twitter = this.getTwitterService();
		
		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		
		long maxId = 0;
		Paging paging = new Paging();
		boolean loadMore = true;
		try {
			List<Status> statuses = twitter.getMentionsTimeline();
			while(loadMore && !statuses.isEmpty()){
				for (Status status : statuses) {
					Date createdAt = status.getCreatedAt();
					if (this.getLastRun().after(createdAt)) {
						loadMore = false;
						break;
					}
					
					Map<String, String> resEntry = new HashMap<String, String>();
					this.addIngredients(status, resEntry);
					result.add(resEntry);
					
					maxId = status.getId();
				}
				maxId--;
				if(maxId < 0){
					break;
				}
				paging.setMaxId(maxId);
				statuses = twitter.getMentionsTimeline(paging);
			}
		} catch (TwitterException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			return null;
		}

		return result;
	}

	
	/*@TriggerTag(name = "New follower", description = "This Trigger fires every time a new user starts following you.")
	public List<Map<String, String>> newFollower(){}*/
	
	// this will not post duplicates (twitter policy)
	@ActionTag(name = "Post a tweet", description = "This action will post a tweet to your Twitter account.")
	public void postTweet(
			@FieldTag(name = "Text", description = "The actual tweet to post.", type = FieldType.TEXT) String text) {

		try {
			Twitter twitter = this.getTwitterService();
			twitter.updateStatus(text);
		} catch (TwitterException e) {
			//e.printStackTrace();
			return;
		}
	}

	
	/**
	 * This method returns an instance of the Twitter service.
	 */
	private Twitter getTwitterService() {
		/*Twitter twitter =  new TwitterFactory().getInstance(
				new AccessToken(this.getChannelConnector().getToken(), this.getChannelConnector().getRefreshToken()));*/
		//Twitter twitter =  new TwitterFactory().getInstance();
		/*if(env == null){
			System.err.println("NULL ENV");
		}
		System.err.println(env.getProperty("twitter.consumer.key"));
		System.err.println(env.getProperty("twitter.consumer.secret"));*/
		/*System.err.println(this.consumerKey);
		System.err.println(this.consumerSecret);*/
		/*if(twitter == null){
			System.err.println("NULL TWITTER");
		}*/
		//twitter.setOAuthConsumer(env.getProperty("twitter.consumer.key"), env.getProperty("twitter.consumer.secret"));
		//twitter.setOAuthConsumer(this.consumerKey, this.consumerSecret);
		/*twitter.setOAuthAccessToken(new AccessToken(this.getChannelConnector().getToken()
				, this.getChannelConnector().getRefreshToken()));
		return twitter;*/
		return new TwitterFactory().getInstance(
				new AccessToken(this.getChannelConnector().getToken(), this.getChannelConnector().getRefreshToken()));
	}

	private void addIngredients(Status status, Map<String, String> ingredients) {
		DateFormat toFormat = new SimpleDateFormat(Validator.TIMESTAMP_FORMAT);
		toFormat.setTimeZone(TimeZone.getTimeZone(this.getUser().getTimezone()));
		
		ingredients.put("Text", status.getText());
		ingredients.put("UserName", status.getUser().getScreenName());
		ingredients.put("CreatedAt", toFormat.format(status.getCreatedAt()));
	}

}
