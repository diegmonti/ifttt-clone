package iftttclone.channels;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import iftttclone.channels.annotation.ActionTag;
import iftttclone.channels.annotation.ActionFieldTag;
import iftttclone.channels.annotation.ChannelTag;
import iftttclone.channels.annotation.IngredientTag;
import iftttclone.channels.annotation.IngredientsTag;
import iftttclone.channels.annotation.TriggerTag;
import iftttclone.channels.annotation.TriggerFieldTag;

@ChannelTag(name = "FakeTwitter", description = "A fake channel for Twitter")
public class FakeTwitterChannel {

	@TriggerTag(name = "New tweet by a specific user", description = "This trigger fires when a user tweets")
	
	@IngredientsTag(
	{ @IngredientTag(name="TweetText", description="The text of the tweet", example="Hi there!"),
	@IngredientTag(name="TweetUrl", description="The url of the tweet", example="http://foo.com/123") })
	public Map<String, String> newTweetByUser(@TriggerFieldTag(name = "Username", description = "User to check") String username, Date lastRun) {
		// Lo scheduler chiama il metodo del trigger passando i field e probabilmente anche l'istante dell'ultima esecuzione
		// Il trigger ritorna null se non bisogna fare nulla, oppure una mappa (eventualmente vuota) contenente gli ingredienti
		// In caso di problemi lancia una eccezione
		// Ad esempio in questo caso <"TweetText", "Testo del tweet di username">
		String tweetText = "This comes from the service";
		Map<String, String> ingredients = new HashMap<String, String>();
		ingredients.put("TweetText", tweetText);
		return ingredients;
	}

	@ActionTag(name = "Post a tweet", description = "Post a tweet in your account")
	public void postTweet(
			@ActionFieldTag(name = "Tweet text", description = "Text of the tweet to be posted") String tweet) {
		// Chiaramente qui posta il tweet, se non funziona lancia una eccezione
	}
}
