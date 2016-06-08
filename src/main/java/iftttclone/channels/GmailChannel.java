package iftttclone.channels;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import iftttclone.channels.annotations.ActionFieldTag;
import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.TriggerFieldTag;
import iftttclone.channels.annotations.TriggerTag;
import iftttclone.entities.ChannelConnector;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.UserRepository;

@ChannelTag(name = "Gmail", description = "A channel for Gmail", withConnection = true)
public class GmailChannel extends AbstractChannel {

	@Autowired
	ChannelConnectorRepository channelConnectorRepository;
	@Autowired
	ChannelRepository channelRepository;
	@Autowired
	UserRepository userRepository;

	@TriggerTag(name = "New Email From with Subject", description = "This trigger fires when the user recives an email from someone with a specific subject")
	@IngredientTag(name = "Sender", description = "The email address of the person who sent the email", example = "somebody@gmail.com")
	@IngredientTag(name = "ReceivedAt", description = "The timestamp of the reception of the email", example = "23/05/2016 13:09")
	@IngredientTag(name = "BodyPlain", description = "The plain text of the email", example = "Hi there!")
	public Map<String, String> newEmailRecived(
			String username,
			@TriggerFieldTag(name = "Sender", description = "The email address of the person who sent the email", canBePublic = false) String sender,
			@TriggerFieldTag(name = "Subject", description = "The subject of the email", canBePublic = true) String subject) {

		try {

			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

			ChannelConnector cc = channelConnectorRepository.getChannelConnectorByChannelAndUser(
					channelRepository.getChannelByClasspath(GmailChannel.class.getName()),
					userRepository.getUserByUsername(username));

			Credential credentials = new GoogleCredential().setAccessToken(cc.getToken());
			Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, credentials).setApplicationName("IFTTT-CLONE")
					.build();

			if (gmail == null)
				System.out.println("gmail is null");
			ListMessagesResponse listResponse = gmail.users().messages().list("me").execute();
			List<Message> messages = listResponse.getMessages();

			for (Message m : messages) {
				System.out.println("\n" + m.getSnippet() + "\n");
			}
			// TODO: remove print and add

		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		// TODO: do something

		Map<String, String> ingredients = new HashMap<String, String>();
		return ingredients;
	}

	@ActionTag(name = "Send an email", description = "Send an email to someone")
	public void sendEmail(
			@ActionFieldTag(name = "Receiver", description = "Email address of the receiver", canBePublic = false) String receiver,
			@ActionFieldTag(name = "Subject", description = "Subject of the email", canBePublic = true) String subject,
			@ActionFieldTag(name = "BodyPlain", description = "The plain text of the email", canBePublic = true) String text) {

		// here we send the email
	}
}
