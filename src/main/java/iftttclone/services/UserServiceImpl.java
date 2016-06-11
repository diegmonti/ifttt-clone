package iftttclone.services;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.core.Utils;
import iftttclone.entities.User;
import iftttclone.exceptions.InvalidRequestException;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.UserService;

@Component
@Transactional
public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;
	@Autowired
	PasswordEncoder passwordEncoder;

	@Override
	public User getUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		return userRepository.getUserByUsername(auth.getName());
	}

	@Override
	public void createUser(User user) {
		String username = user.getUsername();
		String password = user.getPassword();
		String email = user.getEmail();
		String timezone = user.getTimezone();

		if (username == null || password == null || email == null || timezone == null) {
			throw new InvalidRequestException("All fields must be present");
		}

		if (userRepository.getUserByUsername(username) != null) {
			throw new InvalidRequestException("The chosen username is already registered");
		}

		if (password.equals("")) {
			throw new InvalidRequestException("The password cannot be empty");
		}

		if (!Utils.isValidEmail(email)) {
			throw new InvalidRequestException("Enter a valid email address");
		}

		if (!Utils.isValidTimezone(timezone)) {
			throw new InvalidRequestException("Select a valid timezone");
		}

		// Encode password
		user.setPassword(passwordEncoder.encode(password));

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
				throw new InvalidRequestException("The password cannot be empty");
			}
		}

		if (email != null) {
			if (Utils.isValidEmail(email)) {
				user.setEmail(email);
			} else {
				throw new InvalidRequestException("Enter a valid email address");
			}
		}

		if (timezone != null) {
			if (Utils.isValidTimezone(timezone)) {
				user.setTimezone(timezone);
			} else {
				throw new InvalidRequestException("Select a valid timezone");
			}
		}

		userRepository.save(user);
	}

	@Override
	public Set<String> getTimezones() {
		return Utils.getTimezones();
	}

}
