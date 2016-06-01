package iftttclone.channels;

import java.util.HashMap;
import java.util.Map;

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
