package iftttclone.channels;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.gmail.Gmail;
import com.google.api.services.gmail.GmailScopes;
import com.google.api.services.gmail.model.ListMessagesResponse;
import com.google.api.services.gmail.model.Message;

import iftttclone.channels.annotation.ActionFieldTag;
import iftttclone.channels.annotation.ActionTag;
import iftttclone.channels.annotation.ChannelTag;
import iftttclone.channels.annotation.IngredientTag;
import iftttclone.channels.annotation.TriggerFieldTag;
import iftttclone.channels.annotation.TriggerTag;

@ChannelTag(name = "Gmail", description = "A channel for Gmail")
public class GmailChannel extends AbstractChannel {
	
	@TriggerTag(name = "New Email", description = "This trigger fires when the user recives an email")
	@IngredientTag(name="EmailText", description="The text of the email", example="Hi there!")
	@IngredientTag(name="Receiver", description="The email address of the person who will receive the fired email", example="Hi there!")
	public Map<String, String> newEmailRecived(
			@TriggerFieldTag(name = "emailSender", description = "the email address of the person who wrote me") String username,
			@TriggerFieldTag(name = "emailSubject", description = "the subject of the email") String subject,
			@TriggerFieldTag(name = "emailDate", description = "the timestamp of the reception of the email") String dateReceived) {
		
		try{
			
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
			
			GoogleClientSecrets secrets =  GoogleClientSecrets.load(jsonFactory, (Reader)clientSecret);
			java.io.File DATA_STORE_DIR = new java.io.File(
			        System.getProperty("user.home"), ".credentials/gmail-java-quickstart.json");
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			DataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		   
			GoogleAuthorizationCodeFlow flow = 
			   new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,Collections.singleton(GmailScopes.MAIL_GOOGLE_COM))
				   .setDataStoreFactory(dataStoreFactory)				   
				   .build();
			
	   if(flow.loadCredential("username") == null) { // TODO: use username
		   System.err.println("Error: no gmail credentials found for " +  "username");
		   return null; 
	   }
			
	   Gmail gmail = new Gmail.Builder(httpTransport, jsonFactory, flow.loadCredential("username"))
			   .setApplicationName("IFTTT-CLONE")
			   .build();
	   
	   if(gmail == null)System.out.println("gmail is null");
	   ListMessagesResponse listResponse = gmail.users().messages().list("me").execute();
	   List<Message> messages = listResponse.getMessages();
	   
	   
	   for(Message m : messages){
		   System.out.println("\n" + m.getSnippet() + "\n");
	   }
	   // TODO: remove print and add 
	      
    } catch (IOException e) {
      e.printStackTrace();
    } catch(Exception e){
		e.printStackTrace();
	}
		// TODO: do something
		
		Map<String, String> ingredients = new HashMap<String, String>();
		return ingredients;
	}
	
	@ActionTag(name = "Send an email", description = "Send an email to someone")
	public void sendEmail(
			@ActionFieldTag(name = "EmailReceiver", description = "email address of the receiver") String receiver, 
			@ActionFieldTag(name = "EmailSubject", description = "Subject of the email") String subject, 
			@ActionFieldTag(name = "EmailText", description = "Text of the email") String text) {
		
		// here we send the email
	}
}
