package iftttclone.controllers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import iftttclone.services.interfaces.GmailConnectorService;
import iftttclone.utils.Utils;

@RestController
@RequestMapping(value = "/channels/gmail")
public class GmailConnectorController {
	@Autowired
	private GmailConnectorService gmailConnectorService;

	@RequestMapping(value = "/activate", method = RequestMethod.POST)
	public ModelAndView auth(HttpServletRequest req) {
		return new ModelAndView("redirect:" + gmailConnectorService.requestConnection(Utils.getURL(req)));
	}

	@RequestMapping(value = "/authorize", method = RequestMethod.GET)
	public ModelAndView callback(HttpServletRequest req) throws IOException {
		gmailConnectorService.validateConnection(Utils.getURL(req), req.getParameter("code"),
				req.getParameter("state"));
		return new ModelAndView("redirect:/");
	}

}
