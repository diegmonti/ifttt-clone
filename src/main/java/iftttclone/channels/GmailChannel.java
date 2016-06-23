package iftttclone.channels;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.LinkedList;
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

import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.FieldTag;
import iftttclone.channels.annotations.TriggerTag;
import iftttclone.core.Validator.FieldType;
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
	public List<Map<String, String>> newEmailRecived(
			@FieldTag(name = "Sender", description = "The email address of the person who sent the email", type = FieldType.EMAIL, publishable = false) String sender,
			@FieldTag(name = "Subject", description = "The subject of the email", type = FieldType.TEXT) String subject) {

		try {
			Gmail gmail = this.getGmailService();

			if (gmail == null) // is this possible?
				System.out.println("gmail is null");
			ListMessagesResponse listResponse = gmail.users().messages().list("me").execute();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		Map<String, String> resEntry = new HashMap<String, String>();
		return result;
	}

	@ActionTag(name = "Send an email", description = "Send an email to someone")
	public void sendEmail(
			@FieldTag(name = "Receiver", description = "Email address of the receiver", type=FieldType.EMAIL, publishable = false) String receiver,
			@FieldTag(name = "Subject", description = "Subject of the email", type=FieldType.TEXT) String subject,
			@FieldTag(name = "BodyPlain", description = "The plain text of the email", type=FieldType.LONGTEXT) String text) {

		try {
			Gmail gmail = this.getGmailService();
		} catch (GeneralSecurityException | IOException e) { // error 500
			return;
		}
	}

	// Utility methods
	private Gmail getGmailService() throws GeneralSecurityException, IOException {
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		Credential credentials = new GoogleCredential().setAccessToken(this.getChannelConnector().getToken());
		return new Gmail.Builder(httpTransport, jsonFactory, credentials).setApplicationName("IFTTT-CLONE").build();
	}

}
