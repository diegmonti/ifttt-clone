package iftttclone.channels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;
import java.util.TimeZone;

import javax.mail.Message.RecipientType;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.repackaged.org.apache.commons.codec.binary.Base64;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;
import com.google.api.services.gmail.model.MessagePart;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Profile;

import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.FieldTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.TriggerTag;
import iftttclone.core.Validator;
import iftttclone.core.Validator.FieldType;
import iftttclone.exceptions.SchedulerException;

@ChannelTag(name = "Gmail", description = "Gmail is a free email service provided by Google.", withConnection = true)
public class GmailChannel extends AbstractChannel {

	@TriggerTag(name = "New email from with subject", description = "This trigger fires when you recive an email from someone with a specific subject.")
	@IngredientTag(name = "From", description = "The email address of the person who sent the email.", example = "user@example.com")
	@IngredientTag(name = "AcceptedAt", description = "The date and time of the reception of the email.", example = "23/05/2016 13:09")
	@IngredientTag(name = "Body", description = "The text of the email.", example = "Hi there!")
	@IngredientTag(name = "Subject", description = "The subject of the email.", example = "Hello")
	public List<Map<String, String>> newEmailReceived(
			@FieldTag(name = "From", description = "The email address of the person who sent the email.", type = FieldType.EMAIL, publishable = false) String from,
			@FieldTag(name = "Subject", description = "The subject of the email.", type = FieldType.TEXT) String subject) {

		Gmail gmail;
		ListMessagesResponse messages;
		Date lastRun = new Date(this.getLastRun().getTime());
		DateFormat toFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
		try {
			gmail = this.getGmailService();

			messages = gmail.users().messages().list("me")
					.setQ("from:" + from + " subject:" + subject + " after:" + toFormat.format(lastRun)).execute();
		} catch (Exception e) {
			throw new SchedulerException();
		}

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		if (messages.getMessages() == null) {
			return null;
		}

		for (Message message : messages.getMessages()) {
			try {
				message = gmail.users().messages().get("me", message.getId()).execute();
			} catch (IOException e) {
				continue;
			}

			if (message.getInternalDate() == null) {
				continue;
			}
			Date acceptedAt = new Date(message.getInternalDate());

			// Already processed
			if (this.getLastRun().after(acceptedAt)) {
				continue;
			}

			Map<String, String> resEntry = new HashMap<String, String>();
			this.addIngredients(message, resEntry);
			result.add(resEntry);
		}

		return result;
	}

	@ActionTag(name = "Send an email", description = "This action will send an email to someone from your Gmail account.")
	public void sendEmail(
			@FieldTag(name = "To", description = "The email address of the receiver.", type = FieldType.EMAIL, publishable = false) String to,
			@FieldTag(name = "Subject", description = "The subject of the email.", type = FieldType.TEXT) String subject,
			@FieldTag(name = "Body", description = "The text of the email.", type = FieldType.LONGTEXT) String bodyText) {

		Gmail gmail;
		Profile profile;
		try {
			gmail = this.getGmailService();
			profile = gmail.users().getProfile("me").execute();
		} catch (GeneralSecurityException | IOException e) {
			throw new SchedulerException();
		}

		if (profile.getEmailAddress() == null) {
			throw new SchedulerException();
		}

		MimeMessage email;
		Message message;
		try {
			email = this.createEmail(to, profile.getEmailAddress(), subject, bodyText);

			ByteArrayOutputStream bytes = new ByteArrayOutputStream();
			email.writeTo(bytes);
			String encodedEmail = Base64.encodeBase64URLSafeString(bytes.toByteArray());
			message = new Message();
			message.setRaw(encodedEmail);

			gmail.users().messages().send("me", message).execute();
		} catch (Exception e) {
			throw new SchedulerException();
		}
	}

	/**
	 * This method returns an instance of the Gmail service.
	 */
	private Gmail getGmailService() throws GeneralSecurityException, IOException {
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
		GoogleClientSecrets secrets = GoogleClientSecrets.load(jsonFactory, clientSecret);
		Credential credentials = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory)
				.setClientSecrets(secrets).build().setAccessToken(this.getChannelConnector().getToken())
				.setRefreshToken(this.getChannelConnector().getRefreshToken());
		return new Gmail.Builder(httpTransport, jsonFactory, credentials).setApplicationName("IFTTT-CLONE").build();
	}

	private void addIngredients(Message message, Map<String, String> ingredients) {
		DateFormat toFormat = new SimpleDateFormat(Validator.TIMESTAMP_FORMAT);
		toFormat.setTimeZone(TimeZone.getTimeZone(this.getUser().getTimezone()));

		String from = "";
		String bodyText = "";
		String subject = "";
		Date acceptedAt = Calendar.getInstance().getTime();

		MessagePart mp = message.getPayload();
		if (mp != null) {
			if (mp.getHeaders() != null) {
				for (MessagePartHeader mph : mp.getHeaders()) {
					String name = mph.getName();
					if (name == null) {
						continue;
					}

					if (name.equals("From")) {
						from = mph.getValue();
					} else if (name.equals("Subject")) {
						subject = mph.getValue();
					}
				}
			}

			if (mp.getParts() != null) {
				for (MessagePart mpi : mp.getParts()) {
					if ((mpi.getBody() != null) && (mpi.getBody().getData() != null)
							&& (mpi.getMimeType().equals("text/plain"))) {
						bodyText = bodyText + new String(Base64.decodeBase64(mpi.getBody().getData()));
					}
					if (mpi.getParts() != null) {
						for (MessagePart mpij : mpi.getParts()) {
							if ((mpij.getBody() != null) && (mpij.getBody().getData() != null)
									&& (mpij.getMimeType().equals("text/plain"))) {
								bodyText = bodyText + new String(Base64.decodeBase64(mpij.getBody().getData()));
							}
						}
					}
				}
			}
		}

		if (message.getInternalDate() != null) {
			acceptedAt = new Date(message.getInternalDate());
		}

		ingredients.put("From", from);
		ingredients.put("Subject", subject);
		ingredients.put("Body", bodyText);
		ingredients.put("AcceptedAt", toFormat.format(acceptedAt));
	}

	private MimeMessage createEmail(String to, String from, String subject, String bodyText)
			throws MessagingException, AddressException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);

		MimeMessage email = new MimeMessage(session);

		email.setFrom(new InternetAddress(from));
		email.addRecipient(RecipientType.TO, new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);

		return email;
	}

}
