package iftttclone.services;

import java.io.IOException;
import java.security.GeneralSecurityException;

import com.google.api.services.gmail.GmailScopes;

import iftttclone.channels.GmailChannel;
import iftttclone.services.interfaces.GmailConnectorService;

public class GmailConnectorServiceImpl extends GoogleConnectorService implements GmailConnectorService {
	private final static String callback = "/api/channels/gmail/authorize";

	public GmailConnectorServiceImpl() throws GeneralSecurityException, IOException {
		super(GmailChannel.class.getName(), GmailScopes.MAIL_GOOGLE_COM, callback);
	}

}
