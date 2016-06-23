package iftttclone.channels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Properties;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.google.api.services.gmail.model.MessagePartBody;
import com.google.api.services.gmail.model.MessagePartHeader;
import com.google.api.services.gmail.model.Profile;

import iftttclone.channels.annotations.ActionTag;
import iftttclone.channels.annotations.ChannelTag;
import iftttclone.channels.annotations.IngredientTag;
import iftttclone.channels.annotations.FieldTag;
import iftttclone.channels.annotations.TriggerTag;
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

	@TriggerTag(name = "New Email From with Subject", description = "This trigger fires when the user recives an email from someone with a specific word in the subject")
	@IngredientTag(name = "From", description = "The email address of the person who sent the email", example = "somebody@gmail.com")
	@IngredientTag(name = "AcceptedAt", description = "The timestamp of the reception of the email", example = "23/05/2016 13:09 UTC")
	@IngredientTag(name = "BodyText", description = "The plain text of the email", example = "Hi there!")
	@IngredientTag(name = "Subject", description = "The subject of the email", example = "Hi there!")
	public List<Map<String, String>> newEmailReceived(
			@FieldTag(name = "From", description = "The email address of the person who sent the email", publishable = false) String from,
			@FieldTag(name = "Subject", description = "The subject of the email", publishable = true) String subject) {

		Gmail gmail;
		ListMessagesResponse messages;
		Date lastRun = new Date(this.getLastRun().getTime());
		DateFormat toFormat = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
		try {
			gmail = this.getGmailService();

			messages = gmail.users().messages().list("me")
					.setQ("from:"+from+" subject:"+subject+" after:"+toFormat.format(lastRun)).execute();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		List<Map<String, String>> result = new LinkedList<Map<String, String>>();
		if(messages.getMessages() == null){
			return null;
		}
		//System.err.println(messages.getMessages().size());
		for (Message message : messages.getMessages()) {
			try {
				message = gmail.users().messages().get("me", message.getId()).execute();
			} catch (IOException e) {
				e.printStackTrace();
				continue;
			}
			
			if(message.getInternalDate() == null){
				continue;
			}
			//System.err.println(message.getInternalDate());
			/*for (MessagePartHeader mph : message.getPayload().getHeaders()) {
				System.err.println(mph.getValue());
			}*/
			Date acceptedAt = new Date(message.getInternalDate());
			
			if(this.getLastRun().after(acceptedAt)){	// already processed, this may skip some events that were created while executing this function
				continue;
			}
			
			/*MessagePart mp = message.getPayload();
			System.err.println(mp);
			if((mp == null) || (mp.getHeaders() == null)){
				continue;
			}
			System.err.println(mp.getHeaders());
			System.err.println(mp.getHeaders().size());
			String date = "";
			for (MessagePartHeader mph : mp.getHeaders()) {
				String name = mph.getName();
				System.err.println(name);
				if(name == null){
					continue;
				}
				if(name.equals("Date")){
					date = name;
				}
			}
			System.err.println(date);
			if(date.isEmpty()){
				continue;
			}
			
			DateFormat rfc2822Format = new SimpleDateFormat("dd MMMMM yyyy, HH:mm");	// this is not technically correct since it is not rfc2822 compliant
			Date hDate;
			try {
				hDate = rfc2822Format.parse(date);
			} catch (ParseException e) {
				continue;
			}
			
			if(this.getLastRun().after(hDate)){	// already processed, this may skip some events that were created while executing this function
				continue;
			}*/
			
			Map<String, String> resEntry = new HashMap<String, String>();
			this.addIngredients(message, resEntry);
			result.add(resEntry);
		}
		
		return result;
	}

	
	@ActionTag(name = "Send an email", description = "Send an email to someone")
	public void sendEmail(
			@FieldTag(name = "To", description = "Email address of the receiver", publishable = false) String to,
			@FieldTag(name = "Subject", description = "Subject of the email", publishable = true) String subject,
			@FieldTag(name = "BodyText", description = "The plain text of the email", publishable = true) String bodyText) {

		Gmail gmail;
		Profile profile;
		try {
			gmail = this.getGmailService();
			
			profile = gmail.users().getProfile("me").execute();
		} catch (GeneralSecurityException | IOException e) {	// error 500
			return;
		}
		
		if(profile.getEmailAddress() == null){
			return;
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
			e.printStackTrace();
			return;
		}
	}
	
	
	// Utility methods
	private Gmail getGmailService() throws GeneralSecurityException, IOException{
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		//Credential credentials = new GoogleCredential().setAccessToken(this.getChannelConnector().getToken());
		InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
		GoogleClientSecrets secrets = GoogleClientSecrets.load(jsonFactory, clientSecret);
		Credential credentials = new GoogleCredential.Builder()
				.setTransport(httpTransport)
		        .setJsonFactory(jsonFactory)
		        .setClientSecrets(secrets)
		        .build()
		        .setAccessToken(this.getChannelConnector().getToken())
		        .setRefreshToken(this.getChannelConnector().getRefreshToken());
		return new Gmail.Builder(httpTransport, jsonFactory, credentials).setApplicationName("IFTTT-CLONE")
				.build();
	}
	
	private void addIngredients(Message message, Map<String, String> ingredients){	// for scalability only
		DateFormat toFormat = new SimpleDateFormat("dd MMM yyyy HH:mm z", Locale.ENGLISH);
		
		String from = "";
		String bodyText = "";
		String subject = "";
		Date acceptedAt = Calendar.getInstance().getTime();
		
		MessagePart mp = message.getPayload();
		if(mp != null){
			if(mp.getHeaders() != null){
				for (MessagePartHeader mph : mp.getHeaders()) {
					String name = mph.getName();
					if(name == null){
						continue;
					}
					
					if(name.equals("From")){
						from = mph.getValue();
					} else if(name.equals("Subject")){
						subject = mph.getValue();
					}
				}
			}
			//System.err.println(mp.getBody());
			for(MessagePart mpi : mp.getParts()){
				if((mpi.getBody() != null) && (mpi.getBody().getData() != null)
						&& (mpi.getMimeType().equals("text/plain"))){
					bodyText = bodyText + new String(Base64.decodeBase64(mpi.getBody().getData()));
				}
				//System.err.println(mpi);
				//System.err.println(mpi.getParts());
				if(mpi.getParts() != null){
					for(MessagePart mpij : mpi.getParts()){
						if((mpij.getBody() != null) && (mpij.getBody().getData() != null)
								&& (mpij.getMimeType().equals("text/plain"))){
							bodyText = bodyText + new String(Base64.decodeBase64(mpij.getBody().getData()));
						}
					}
					/*System.err.println(mpi.getParts().get(0).getBody().getData());
					System.err.println(new String(Base64.decodeBase64(mpi.getParts().get(0).getBody().getData())));*/
				}
				/*System.err.println(mpi.getMimeType());
				System.err.println(mpi.getBody());*/
				/*if((mpi.getBody() != null) && (mpi.getBody().getData() != null)
						&& (mpi.getMimeType().equals("text/plain") || mpi.getMimeType().equals("text/html"))){
					//bodyText = bodyText + mpi.getBody().getData();
					bodyText = bodyText + new String(Base64.decodeBase64(mpi.getBody().getData()));
				}*/
			}
			/*System.err.println(mp.getBody().getData());
			if((mp.getBody() != null) && (mp.getBody().getData() != null)){
				bodyText = mp.getBody().getData();
			}*/
		}
		if(message.getInternalDate() != null){
			acceptedAt = new Date(message.getInternalDate());
		}
		
		ingredients.put("From", from);
		ingredients.put("Subject", subject);
		ingredients.put("BodyText", bodyText);
		ingredients.put("AcceptedAt", toFormat.format(acceptedAt));
	}
	
	private MimeMessage createEmail(String to, String from, String subject, String bodyText) throws MessagingException, AddressException {
		Properties props = new Properties();
		Session session = Session.getDefaultInstance(props, null);
		
		MimeMessage email = new MimeMessage(session);
		
		email.setFrom(new InternetAddress(from));
		email.addRecipient(javax.mail.Message.RecipientType.TO,
		                   new InternetAddress(to));
		email.setSubject(subject);
		email.setText(bodyText);
		return email;
	}
}
