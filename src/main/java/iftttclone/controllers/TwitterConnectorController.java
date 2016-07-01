package iftttclone.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import iftttclone.services.interfaces.TwitterConnectorService;

@RestController
@RequestMapping(value = "/channels/twitter")
public class TwitterConnectorController {
	@Autowired
	private TwitterConnectorService twitterConnectorService;

	@CrossOrigin
	@RequestMapping(value = "/activate", method = RequestMethod.POST)
	public ModelAndView auth(HttpServletRequest req) {
		String rdct = twitterConnectorService.requestConnection("");
		if(rdct == null){
			return new ModelAndView("redirect:/#/channel/twitter");
		}
		return new ModelAndView("redirect:" + rdct);
	}

	@RequestMapping(value = "/authorize", method = RequestMethod.GET)
	public ModelAndView callback(HttpServletRequest req) {
		twitterConnectorService.validateConnection("", req.getParameter("oauth_verifier"), req.getParameter("oauth_token"));
		return new ModelAndView("redirect:/#/channel/twitter");
	}
	
	@RequestMapping(value = "/deactivate", method = RequestMethod.POST)
	public void deauth(HttpServletRequest req) {
		twitterConnectorService.removeConnection();
	}

}
