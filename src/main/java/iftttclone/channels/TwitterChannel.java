package iftttclone.channels;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.FieldTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.TriggerTag;
import iftttclone.core.Validator;
import iftttclone.core.Validator.FieldType;
import iftttclone.exceptions.SchedulerException;
import twitter4j.Paging;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;

/**
 * The Twitter API makes some restriction about the maximum number of tweets to
 * retrieve (the newest 3200), as well as the number of read calls in a window
 * (15 every 15 minutes per user), write calls limits are shared for all
 * applications in a per user basis.
 */
@ChannelTag(name = "Twitter", description = "Twitter is a free online social networking service that enables users to send and read short 140-character messages called \"tweets\".", withConnection = true, permits = "Read and post tweets")
public class TwitterChannel extends AbstractChannel {

	@TriggerTag(name = "New tweet by you", description = "This trigger fires when you post a new tweet.")
	@IngredientTag(name = "Text", description = "The actual tweet.", example = "I am eating something")
	@IngredientTag(name = "UserName", description = "Username of tweeter.", example = "twitterMan")
	@IngredientTag(name = "CreatedAt", description = "The date and time of the reception of the tweet.", example = "23/05/2016 13:09")
	public List<Map<String, String>> newTweetByYou() {

		Twitter twitter = this.getTwitterService();

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();

		long maxId = 0;
		Paging paging = new Paging();
		boolean loadMore = true;
		try {
			List<Status> statuses = twitter.getUserTimeline();
			while (loadMore && !statuses.isEmpty()) {
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
				if (maxId < 0) {
					break;
				}
				paging.setMaxId(maxId);
				statuses = twitter.getUserTimeline(paging);
			}
		} catch (TwitterException e) {
			throw new SchedulerException();
		}

		return result;
	}

	@TriggerTag(name = "New mention of you", description = "This trigger fires every time you are @mentioned in a tweet.")
	@IngredientTag(name = "Text", description = "The actual tweet.", example = "I am eating something")
	@IngredientTag(name = "UserName", description = "Username of tweeter.", example = "twitterMan")
	@IngredientTag(name = "CreatedAt", description = "The date and time of the reception of the tweet.", example = "23/05/2016 13:09")
	public List<Map<String, String>> newMentionOfYou() {

		Twitter twitter = this.getTwitterService();

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();

		long maxId = 0;
		Paging paging = new Paging();
		boolean loadMore = true;
		try {
			List<Status> statuses = twitter.getMentionsTimeline();
			while (loadMore && !statuses.isEmpty()) {
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
				if (maxId < 0) {
					break;
				}
				paging.setMaxId(maxId);
				statuses = twitter.getMentionsTimeline(paging);
			}
		} catch (TwitterException e) {
			throw new SchedulerException();
		}

		return result;
	}

	/**
	 * This will not post duplicates because of Twitter policy.
	 * @param text the text of the tweet
	 */
	@ActionTag(name = "Post a tweet", description = "This action will post a tweet to your Twitter account.")
	public void postTweet(
			@FieldTag(name = "Text", description = "The actual tweet to post.", type = FieldType.TEXT) String text) {

		try {
			Twitter twitter = this.getTwitterService();
			twitter.updateStatus(text);
		} catch (TwitterException e) {
			throw new SchedulerException();
		}
	}

	/**
	 * This method returns an instance of the Twitter service.
	 * @return twitter service
	 */
	private Twitter getTwitterService() {
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
