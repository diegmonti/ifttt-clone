package iftttclone.controllers;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import iftttclone.entities.User;
import iftttclone.services.interfaces.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	@Autowired
	UserService userService;

	@RequestMapping(value = "", method = RequestMethod.GET)
	public User user() {
		return userService.getUser();
	}

	@RequestMapping(value = "", method = RequestMethod.POST)
	@ResponseStatus(HttpStatus.CREATED)
	public void createUser(@RequestBody User user) {
		userService.createUser(user);
	}

	@RequestMapping(value = "", method = RequestMethod.PUT)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void updateUser(@RequestBody User user) {
		userService.updateUser(user);
	}

	@RequestMapping(value = "/timezones", method = RequestMethod.GET)
	public Set<String> getTimezones() {
		return userService.getTimezones();
	}
}
