package iftttclone.core;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import iftttclone.services.ChannelServiceImpl;
import iftttclone.services.GmailConnectorServiceImpl;
import iftttclone.services.GoogleCalendarConnectorServiceImpl;
import iftttclone.services.UserServiceImpl;
import iftttclone.services.interfaces.ChannelService;
import iftttclone.services.interfaces.GmailConnectorService;
import iftttclone.services.interfaces.GoogleCalendarConnectorService;
import iftttclone.services.interfaces.UserService;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "iftttclone")
public class WebConfig {

	@Bean
	public ChannelService channelService() {
		return new ChannelServiceImpl();
	}

	@Bean
	public UserService userService() {
		return new UserServiceImpl();
	}

	@Bean
	public GmailConnectorService gmailConnectorService() throws GeneralSecurityException, IOException {
		return new GmailConnectorServiceImpl();
	}

	@Bean
	public GoogleCalendarConnectorService googleCalendarConnectorService()
			throws GeneralSecurityException, IOException {
		return new GoogleCalendarConnectorServiceImpl();
	}

}