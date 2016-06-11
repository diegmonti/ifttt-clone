package iftttclone.services;

import java.io.IOException;
import java.security.GeneralSecurityException;

import org.springframework.stereotype.Component;

import com.google.api.services.calendar.CalendarScopes;

import iftttclone.channels.GoogleCalendarChannel;
import iftttclone.services.interfaces.GoogleCalendarConnectorService;

@Component
public class GoogleCalendarConnectorServiceImpl extends GoogleConnectorService implements GoogleCalendarConnectorService {
	private final static String callback = "/api/channels/google_calendar/authorize";

	public GoogleCalendarConnectorServiceImpl() throws GeneralSecurityException, IOException {
		super(GoogleCalendarChannel.class.getName(), CalendarScopes.CALENDAR, callback);
	}

}