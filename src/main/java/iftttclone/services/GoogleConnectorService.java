package iftttclone.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collections;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;

import iftttclone.entities.Channel;
import iftttclone.entities.ChannelConnector;
import iftttclone.entities.User;
import iftttclone.exceptions.ForbiddenException;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.AbstractConnectorService;

public abstract class GoogleConnectorService implements AbstractConnectorService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private ChannelConnectorRepository channelConnectorRepository;

	private JsonFactory jsonFactory;
	private GoogleClientSecrets secrets;
	private HttpTransport httpTransport;

	private String channelPath;
	private String scope;
	private String callback;

	public GoogleConnectorService(String channelPath, String scope, String callback)
			throws GeneralSecurityException, IOException {
		jsonFactory = JacksonFactory.getDefaultInstance();
		InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
		secrets = GoogleClientSecrets.load(jsonFactory, clientSecret);
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		this.channelPath = channelPath;
		this.scope = scope;
		this.callback = callback;
	}

	@Override
	@Transactional
	public String requestConnection(String path) {
		// Get the current user
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.getUserByUsername(auth.getName());

		// Get channel
		Channel channel = channelRepository.getChannelByClasspath(channelPath);

		// Get channel connector
		ChannelConnector channelConnector = channelConnectorRepository.getChannelConnectorByChannelAndUser(channel,
				user);

		if (channelConnector == null) {
			channelConnector = new ChannelConnector();
			channelConnector.setChannel(channel);
			channelConnector.setUser(user);
		}

		// Because we are trying to create a new connection
		channelConnector.setConnectionTime(null);

		// To avoid malicious requests
		String uuid = UUID.randomUUID().toString();
		channelConnector.setToken(uuid);

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,
				Collections.singleton(scope)).setAccessType("offline").build();
		String url = flow.newAuthorizationUrl().setRedirectUri(path + callback).setState(uuid).build();

		channelConnectorRepository.save(channelConnector);

		return url;
	}

	@Override
	public void validateConnection(String path, String code, String token) throws IOException {
		// Get the current user
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.getUserByUsername(auth.getName());

		// Get channel
		Channel channel = channelRepository.getChannelByClasspath(channelPath);

		// Get channel connector
		ChannelConnector channelConnector = channelConnectorRepository.getChannelConnectorByChannelAndUser(channel,
				user);

		if (channelConnector == null) {
			throw new ForbiddenException();
		}

		if (!channelConnector.getToken().equals(token)) {
			throw new ForbiddenException();
		}

		GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,
				Collections.singleton(scope)).setAccessType("offline").build();

		// Exchange authorization code for user credentials
		GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(path + callback).execute();

		channelConnector.setToken(tokenResponse.getAccessToken());
		channelConnector.setRefreshToken(tokenResponse.getRefreshToken());
		channelConnector.setConnectionTime(System.currentTimeMillis());

		channelConnectorRepository.save(channelConnector);
	}

}
