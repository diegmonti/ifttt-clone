package iftttclone.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import iftttclone.core.Utils;
import iftttclone.services.interfaces.GoogleCalendarConnectorService;

@RestController
@RequestMapping(value = "/channels/google_calendar")
public class GoogleCalendarConnectorController {
	@Autowired
	private GoogleCalendarConnectorService googleCalendarConnectorService;

	@RequestMapping(value = "/activate", method = RequestMethod.POST)
	public ModelAndView auth(HttpServletRequest req) {
		return new ModelAndView("redirect:" + googleCalendarConnectorService.requestConnection(Utils.getURL(req)));
	}

	@RequestMapping(value = "/authorize", method = RequestMethod.GET)
	public ModelAndView callback(HttpServletRequest req) throws IOException {
		googleCalendarConnectorService.validateConnection(Utils.getURL(req), req.getParameter("code"),
				req.getParameter("state"));
		return new ModelAndView("redirect:/");
	}
}
