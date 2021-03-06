package iftttclone.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.core.TimezoneManager;
import iftttclone.core.Utils;
import iftttclone.core.Validator;
import iftttclone.entities.ChannelConnector;
import iftttclone.entities.User;
import iftttclone.exceptions.InvalidRequestException;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.UserService;

@Component
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private TimezoneManager timezoneManager;
	@Autowired
	private Utils utils;

	@Override
	public User getUser() {
		User user = utils.getCurrentUser();
		if(user != null){
			Set<ChannelConnector> channelConnectors = user.getChannelConnectors();
			for(ChannelConnector channelConnector : channelConnectors){
				channelConnector.setPermits(channelConnector.getChannel().getPermits());
			}
		}
		return user;
	}

	@Override
	public void createUser(User user) {
		String username = user.getUsername();
		String password = user.getPassword();
		String email = user.getEmail();
		String timezone = user.getTimezone();

		if (username == null || password == null || email == null || timezone == null) {
			throw new InvalidRequestException("All fields must be present.");
		}

		if (userRepository.getUserByUsername(username) != null) {
			throw new InvalidRequestException("The chosen username is already registered.");
		}

		if (password.equals("")) {
			throw new InvalidRequestException("The password cannot be empty.");
		}

		if (!Validator.isValidEmail(email)) {
			throw new InvalidRequestException("Enter a valid email address.");
		}

		user.setPassword(passwordEncoder.encode(password));
		user.setTimezone(timezoneManager.getIdFromName(timezone));

		userRepository.save(user);
	}

	@Override
	public void updateUser(User stub) {
		String password = stub.getPassword();
		String email = stub.getEmail();
		String timezone = stub.getTimezone();

		// Get current user
		User user = getUser();

		if (password != null) {
			if (!password.equals("")) {
				user.setPassword(passwordEncoder.encode(password));
			} else {
				throw new InvalidRequestException("The password cannot be empty.");
			}
		}

		if (email != null) {
			if (Validator.isValidEmail(email)) {
				user.setEmail(email);
			} else {
				throw new InvalidRequestException("Enter a valid email address.");
			}
		}

		if (timezone != null) {
			user.setTimezone(timezoneManager.getIdFromName(timezone));
		}

		userRepository.save(user);
	}

	@Override
	public Set<String> getTimezones() {
		return timezoneManager.getTimezones();
	}

}
