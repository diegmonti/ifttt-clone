package iftttclone.controllers;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collections;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.gmail.GmailScopes;

import iftttclone.entities.User;

@RestController
@RequestMapping(value="/google")
public class GoogleAuthController {

	
	@RequestMapping(value="/mail/auth", method = RequestMethod.GET)
	public ResponseEntity<String> hasGmailRegisterd(){
		
		try{
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
		
		GoogleClientSecrets secrets =  GoogleClientSecrets.load(jsonFactory, (Reader)clientSecret);
		java.io.File DATA_STORE_DIR = new java.io.File(
		        System.getProperty("user.home"), ".credentials/google_tokens.json"); // TODO: save in a more useful location. Maybe web-inf?
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		DataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
	   
		GoogleAuthorizationCodeFlow flow = 
		   new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,Collections.singleton(GmailScopes.MAIL_GOOGLE_COM))
			   .setDataStoreFactory(dataStoreFactory)				   
			   .build();
		
		
		// getting current user
		User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		
		if (flow.loadCredential(user.getUsername()) != null) return new ResponseEntity<>(HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value="/mail/auth", method = RequestMethod.POST)
	@CrossOrigin()
	public ResponseEntity<String> gmailAuth(HttpServletRequest req, HttpServletResponse res){
		try{
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
			
			GoogleClientSecrets secrets =  GoogleClientSecrets.load(jsonFactory, (Reader)clientSecret);
			java.io.File DATA_STORE_DIR = new java.io.File(
			        System.getProperty("user.home"), ".credentials/google_tokens.json");
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			DataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		   
			GoogleAuthorizationCodeFlow flow = 
			   new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,Collections.singleton(GmailScopes.MAIL_GOOGLE_COM))
				   .setDataStoreFactory(dataStoreFactory)				   
				   .build();
			
			User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			if (flow.loadCredential(user.getUsername()) != null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			
			
			
			res.sendRedirect(
					flow.newAuthorizationUrl()
						.setRedirectUri("http://localhost:8080/ifttt-clone/api/google/mail/callback") // google does not accept relative path
						.build()
					);
			
			}catch(Exception e){
				e.printStackTrace();
			}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/calendar/auth", method = RequestMethod.GET)
	public ResponseEntity<String> hasCalendarRegisterd(@PathVariable String id){
		
		try{
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
		
		GoogleClientSecrets secrets =  GoogleClientSecrets.load(jsonFactory, (Reader)clientSecret);
		java.io.File DATA_STORE_DIR = new java.io.File(
		        System.getProperty("user.home"), ".credentials/google_tokens.json");
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		DataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
	   
		GoogleAuthorizationCodeFlow flow = 
		   new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,Collections.singleton(CalendarScopes.CALENDAR))
			   .setDataStoreFactory(dataStoreFactory)				   
			   .build();
		
		if (flow.loadCredential(id) != null) return new ResponseEntity<>(HttpStatus.OK);
		}catch(Exception e){
			e.printStackTrace();
		}
		return new ResponseEntity<>(HttpStatus.NOT_FOUND);
	}
	
	@RequestMapping(value="/calendar/auth", method = RequestMethod.POST)
	public ResponseEntity<String> calendarAuth(HttpServletRequest req, HttpServletResponse res, @PathVariable String id){
		try{
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
			InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
			
			GoogleClientSecrets secrets =  GoogleClientSecrets.load(jsonFactory, (Reader)clientSecret);
			java.io.File DATA_STORE_DIR = new java.io.File(
			        System.getProperty("user.home"), ".credentials/google_tokens.json");
			HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
			DataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
		   
			GoogleAuthorizationCodeFlow flow = 
			   new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,Collections.singleton(CalendarScopes.CALENDAR))
				   .setDataStoreFactory(dataStoreFactory)				   
				   .build();
			
			if (flow.loadCredential(id) != null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			
			res.sendRedirect(
					flow.newAuthorizationUrl()
						.setRedirectUri("http://localhost:8080")
						.build()
					);
			
			}catch(Exception e){
				e.printStackTrace();
			}
		return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
	}
	
	@RequestMapping(value="/mail/callback", method = RequestMethod.GET)
	public void googleCallback(HttpServletRequest req, HttpServletResponse res){
		try{
		JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
		InputStreamReader clientSecret = new InputStreamReader(getClass().getResourceAsStream("/client_secret.json"));
		
		GoogleClientSecrets secrets =  GoogleClientSecrets.load(jsonFactory, (Reader)clientSecret);
		java.io.File DATA_STORE_DIR = new java.io.File(
		        System.getProperty("user.home"), ".credentials/google_tokens.json");
		HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
		DataStoreFactory dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);
	   
		GoogleAuthorizationCodeFlow flow = 
		   new GoogleAuthorizationCodeFlow.Builder(httpTransport, jsonFactory, secrets,Collections.singleton(GmailScopes.MAIL_GOOGLE_COM))
			   .setDataStoreFactory(dataStoreFactory)				   
			   .build();
		// Note that this implementation does not handle the user denying authorization.
	    
	    // Exchange authorization code for user credentials.
	    GoogleTokenResponse tokenResponse = flow.newTokenRequest(req.getParameter("code"))
	        .setRedirectUri("http://localhost:8080/api/google/mail/callback").execute();
	    // Save the credentials for this user so we can access them from the main servlet.
	    flow.createAndStoreCredential(tokenResponse, "user");
	    res.sendRedirect("index.html");
	    
		}catch(Exception e){
			e.printStackTrace();
		}
	    
	}
}
