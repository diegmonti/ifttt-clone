package iftttclone.controllers;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;
import java.util.Date;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.GmailScopes;

import iftttclone.channels.GmailChannel;
import iftttclone.entities.ChannelConnector;
import iftttclone.entities.SecurityUser;
import iftttclone.repositories.ChannelConnectorRepository;
import iftttclone.repositories.ChannelRepository;
import iftttclone.repositories.UserRepository;


@RestController
@RequestMapping(value="/google")
@CrossOrigin
public class GoogleAuthController {

	private ConcurrentHashMap<String, String> generatedUUID;
	
	@Autowired
	ChannelConnectorRepository channelConnectorRepository;	
	@Autowired
	ChannelRepository channelRepository;
	@Autowired
	UserRepository userRepository;
	
	public GoogleAuthController(){
		generatedUUID = new ConcurrentHashMap<String, String>();
	}
	
	
	// TODO: DISCUSS IF IT IS TO KEEP
	@RequestMapping(value="/mail/auth", method = RequestMethod.GET)
	public ResponseEntity<String> hasGmailRegisterd(){
		
		String user = null;
		if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String)
			user = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		else if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SecurityUser)
			user = ((SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
		
		if(user == null) return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		
		
		ChannelConnector cc = channelConnectorRepository.getChannelConnectorByChannelAndUser(
				channelRepository.getChannelByClasspath(GmailChannel.class.getName()), 
    			userRepository.getUserByUsername(user)
				);
		
		if(cc == null)		
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		else
			return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/mail/auth", method = RequestMethod.POST)
	public String gmailAuth(HttpServletRequest req, HttpServletResponse res){
		try{
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
			
			GoogleClientSecrets secrets =  GoogleClientSecrets.load(jsonFactory, (Reader)clientSecret);
			
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			
			GoogleAuthorizationCodeFlow flow = 
			   new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,Collections.singleton(GmailScopes.MAIL_GOOGLE_COM))
				   .setAccessType("offline")
				   .build();
			
			String uuid = UUID.randomUUID().toString();
			
			String user = null;
			if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String)
				user = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			else if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SecurityUser)
				user = ((SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
			
			
			// if (flow.loadCredential(user.getUsername()) != null) return null; // return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			String url = flow.newAuthorizationUrl()
					.setRedirectUri("http://localhost:8080/ifttt-clone/api/google/mail/callback") // google does not accept relative path
					.setState(uuid)
					.build();
			
			generatedUUID.put(uuid, user);
			
			return "{\"url\": \""+url+"\"}";
			
			}catch(Exception e){
				e.printStackTrace();
			}
		return null ;//new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/calendar/auth", method = RequestMethod.GET)
	public ResponseEntity<String> hasCalendarRegisterd(){
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value="/calendar/auth", method = RequestMethod.POST)
	public String calendarAuth(HttpServletRequest req, HttpServletResponse res){
		try{
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
			
			GoogleClientSecrets secrets =  GoogleClientSecrets.load(jsonFactory, (Reader)clientSecret);
			
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			
			GoogleAuthorizationCodeFlow flow = 
			   new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,Collections.singleton(CalendarScopes.CALENDAR))
				   .build();

			String uuid = UUID.randomUUID().toString();
			
			String url = 
					flow.newAuthorizationUrl()
						.setRedirectUri("http://localhost:8080/ifttt-clone/api/google/calendar/callback")
						.setState(uuid)
						.build();
			
			
			String user = null;
			if (SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof String)
				user = (String)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			else if(SecurityContextHolder.getContext().getAuthentication().getPrincipal() instanceof SecurityUser)
				user = ((SecurityUser)SecurityContextHolder.getContext().getAuthentication().getPrincipal()).getUsername();
			generatedUUID.put(uuid, user);
			
			
			return "{\"url\": \""+url+"\"}";
			
			}catch(Exception e){
				e.printStackTrace();
			}
		return null;
	}
	
	@RequestMapping(value="/mail/callback", method = RequestMethod.GET)
	public void googleMailCallback(HttpServletRequest req, HttpServletResponse res){
		try{
			
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			
			InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
			GoogleClientSecrets secrets =  GoogleClientSecrets.load(jsonFactory, (Reader)clientSecret);
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			
		   
			GoogleAuthorizationCodeFlow flow = 
			   new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,Collections.singleton(GmailScopes.MAIL_GOOGLE_COM))
				   .setAccessType("offline")
				   .build();
			
		    // Exchange authorization code for user credentials.
		    GoogleTokenResponse tokenResponse = flow.newTokenRequest(req.getParameter("code"))
		        .setRedirectUri("http://localhost:8080/ifttt-clone/api/google/mail/callback")
		        .execute();
		    
		    System.out.println("tokenResponse.accessToken = " + tokenResponse.getAccessToken());
		    System.out.println("tokenResponse.refresh = " + tokenResponse.getRefreshToken());
		    
		    String user = generatedUUID.get(req.getParameter("state"));
		    // Save the credentials for this user so we can access them from the main servlet.
		    System.out.println("callback. user = " + user);
		    
		    
		    
		    try
		    {
		    	ChannelConnector cc = channelConnectorRepository.getChannelConnectorByChannelAndUser(
		    			channelRepository.getChannelByClasspath(GmailChannel.class.getName()), 
		    			userRepository.getUserByUsername(user));
		    	if(cc== null) cc = new ChannelConnector();
		    	cc.setChannel(channelRepository.getChannelByClasspath(GmailChannel.class.getName()));
		    	cc.setUser(userRepository.getUserByUsername(user));
		    	cc.setCreationTime(new Date());
		    	cc.setToken(tokenResponse.getAccessToken());
		    	// TODO: ADD ACCESS TOKEN
		    	channelConnectorRepository.save(cc);
		    }
		    catch(Exception e){
		    	e.printStackTrace(System.out);
		    }
		    
		    // i need to remove the line from the map
		    generatedUUID.remove(req.getParameter("state"));
		    
	    res.sendRedirect( req.getLocalName() + "/index.html"); // TODO: this redirect is wrong. Send the new URL so the 
	    
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}
