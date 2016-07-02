package iftttclone.services;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.LinkedList;

import org.springframework.stereotype.Component;

import com.google.api.services.gmail.GmailScopes;

import iftttclone.channels.GmailChannel;
import iftttclone.services.interfaces.GmailConnectorService;

@Component
public class GmailConnectorServiceImpl extends GoogleConnectorService implements GmailConnectorService {
	private final static String callback = "/api/channels/gmail/authorize";

	public GmailConnectorServiceImpl() throws GeneralSecurityException, IOException {
		super(GmailChannel.class.getName(), new LinkedList<String>(Arrays.asList(GmailScopes.GMAIL_READONLY, GmailScopes.GMAIL_SEND)), callback);
	}

}
