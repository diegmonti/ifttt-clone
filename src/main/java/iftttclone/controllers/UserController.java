package iftttclone.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import iftttclone.services.interfaces.UserService;

@RestController
@RequestMapping("/users")
public class UserController {

	@Autowired
	UserService userService;
	
	@RequestMapping(value = "/auth", method = RequestMethod.POST)
	public ResponseEntity<?> authUser(HttpServletRequest request, @RequestParam("username") String username,
							@RequestParam("password") String password){
		
		boolean loggedIn = userService.authUser(username, password);
		
		if(loggedIn){
			// i need to put into the session the UserID
			Long userID = userService.getUser(username).getId();
			request.getSession().setAttribute("userID", userID);
			return new ResponseEntity<>(HttpStatus.OK);
		}
		else
		{
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		}
		
	}
}
