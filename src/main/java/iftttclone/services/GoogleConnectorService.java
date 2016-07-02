package iftttclone.services;

import java.io.IOException;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.plus.Plus;
import com.google.api.services.plus.PlusScopes;
import com.google.api.services.plus.model.Person;
import com.google.api.services.plus.model.Person.Emails;

import iftttclone.core.Utils;
import iftttclone.entities.Channel;
import iftttclone.entities.ChannelConnector;
import iftttclone.entities.User;
import iftttclone.exceptions.ForbiddenException;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.RecipeRepository;
import iftttclone.services.interfaces.AbstractConnectorService;

public abstract class GoogleConnectorService implements AbstractConnectorService {
	@Autowired
	private ChannelRepository channelRepository;
	@Autowired
	private ChannelConnectorRepository channelConnectorRepository;
	@Autowired
	private RecipeRepository recipeRepository;
	@Autowired
	private Utils utils;

	private JsonFactory jsonFactory;
	private GoogleClientSecrets secrets;
	private HttpTransport httpTransport;

	private String channelPath;
	private Collection<String> scopes;
	private String callback;

	public GoogleConnectorService(String channelPath, Collection<String> scopes, String callback)
			throws GeneralSecurityException, IOException {
		jsonFactory = JacksonFactory.getDefaultInstance();
		InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
		secrets = GoogleClientSecrets.load(jsonFactory, clientSecret);
		httpTransport = GoogleNetHttpTransport.newTrustedTransport();

		this.channelPath = channelPath;
		scopes.add(PlusScopes.USERINFO_EMAIL);	// deprecated but maintained
		this.scopes = scopes;
		this.callback = callback;
	}

	@Override
	public String requestConnection(String path) {
		// Get the current user
		User user = utils.getCurrentUser();

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
				scopes).setAccessType("offline").build();
		String url = flow.newAuthorizationUrl().setRedirectUri(path + callback).setState(uuid).build();

		channelConnectorRepository.save(channelConnector);

		return url;
	}

	@Override
	public void validateConnection(String path, String code, String token) {
		// Get the current user
		User user = utils.getCurrentUser();

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
				scopes).setAccessType("offline").build();

		// Exchange authorization code for user credentials
		try {
			GoogleTokenResponse tokenResponse = flow.newTokenRequest(code).setRedirectUri(path + callback).execute();

			if (tokenResponse.getRefreshToken() == null) {
				// This should not happen
				return;
			}
			
			Credential credentials = new GoogleCredential.Builder().setTransport(httpTransport).setJsonFactory(jsonFactory)
					.setClientSecrets(secrets).build().setAccessToken(tokenResponse.getAccessToken())
					.setRefreshToken(tokenResponse.getRefreshToken());
			Plus plus = new Plus.Builder(httpTransport, jsonFactory, credentials).setApplicationName("IFTTT-CLONE").build();
			Person person = plus.people().get("me").execute();
			String account = null;
			for(Emails email : person.getEmails()){
				if(email.getType().equals("account")){
					account = email.getValue();
				}
			}
			if(account == null){
				return;
			}

			channelConnector.setToken(tokenResponse.getAccessToken());
			channelConnector.setRefreshToken(tokenResponse.getRefreshToken());
			channelConnector.setConnectionTime(System.currentTimeMillis());
			channelConnector.setAccount(account);

			channelConnectorRepository.save(channelConnector);
		} catch (IOException e) {
			
		}

	}

	@Override
	public void removeConnection() {
		// Get the current user
		User user = utils.getCurrentUser();

		// Get channel
		Channel channel = channelRepository.getChannelByClasspath(channelPath);

		// Get channel connector
		ChannelConnector channelConnector = channelConnectorRepository.getChannelConnectorByChannelAndUser(channel,
				user);

		HttpRequestFactory factory = httpTransport.createRequestFactory();
		GenericUrl url = new GenericUrl(
				"https://accounts.google.com/o/oauth2/revoke?token=" + channelConnector.getRefreshToken());

		try {
			factory.buildGetRequest(url).executeAsync();
		} catch (IOException e) {

		}

		// This could leave us without refreshToken (and the user will not be
		// asked when connecting again since it is already connected)
		// In this case the user must remove the connection manually from Google
		channelConnectorRepository.delete(channelConnector);
		recipeRepository.setAllInactiveByUserAndChannel(user, channel);

	}

}
