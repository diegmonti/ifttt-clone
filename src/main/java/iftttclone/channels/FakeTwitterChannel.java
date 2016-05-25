package iftttclone.channels;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Channel(name = "FakeTwitter", description = "A fake channel for Twitter")
public class FakeTwitterChannel {

	@Trigger(name = "New tweet by a specific user", description = "This trigger fires when a user tweets")
	public Map<String, String> newTweetByUser(@TriggerField(name = "Username", description = "User to check") String username, Date lastRun) {
		// Lo scheduler chiama il metodo del trigger passando i field e probabilmente anche l'istante dell'ultima esecuzione
		// Il trigger ritorna null se non bisogna fare nulla, oppure una mappa (eventualmente vuota) contenente gli ingredienti
		// In caso di problemi lancia una eccezione
		// Ad esempio in questo caso <"TweetText", "Testo del tweet di username">
		@Ingredient(name="TweetText", description="The text of the tweet", example="Hi there!")
		String tweetText = "This comes from the service";
		Map<String, String> ingredients = new HashMap<String, String>();
		ingredients.put("TweetText", tweetText);
		return ingredients;
	}

	@Action(name = "Post a tweet", description = "Post a tweet in your account")
	public void postTweet(
			@ActionField(name = "Tweet text", description = "Text of the tweet to be posted") String tweet) {
		// Chiaramente qui posta il tweet, se non funziona lancia una eccezione
	}
}
