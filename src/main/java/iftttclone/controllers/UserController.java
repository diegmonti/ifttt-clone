package iftttclone.controllers;

import java.security.Principal;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import iftttclone.entities.User;
import iftttclone.services.interfaces.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserService userService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public Principal user(Principal user) {
		return user;
	}
	
	@RequestMapping(value="", method = RequestMethod.POST)
	public ModelAndView createUser( @RequestParam("username") String username, 
									@RequestParam("password") String password,
									@RequestParam("email") String email){
		
		
		User user = new User();
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		
		Long id = userService.createOrUpdateUser(user);
		if(id == null){
			return new ModelAndView("redirect:/#signIn");
		}
		else
		 return new ModelAndView("redirect:/");
	}

}
