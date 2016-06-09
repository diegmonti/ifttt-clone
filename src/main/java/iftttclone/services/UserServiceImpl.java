package iftttclone.services;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.TreeSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import iftttclone.entities.User;
import iftttclone.exceptions.InvalidRequestException;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.UserService;

public class UserServiceImpl implements UserService {
	private static final String TIMEZONE_ID = "^(Africa|America|Asia|Atlantic|Australia|Europe|Indian|Pacific)/.*";

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
		checkUser(user);

		// Encode password
		user.setPassword(passwordEncoder.encode(user.getPassword()));

		userRepository.save(user);
	}

	@Override
	public void updateUser(User user) {
		// TODO Auto-generated method stub

	}

	private void checkUser(User user) {
		String username = user.getUsername();
		String password = user.getPassword();
		String email = user.getEmail();
		String timezone = user.getTimezone();

		if (username == null || password == null || email == null || timezone == null) {
			throw new InvalidRequestException();
		}

		// Password requirements
		if (password.equals("")) {
			throw new InvalidRequestException();
		}

		// Email requirements
		if (!email.contains("@")) {
			throw new InvalidRequestException();
		}

		List<String> allTimezones = Arrays.asList(TimeZone.getAvailableIDs());
		if (!allTimezones.contains(timezone)) {
			throw new InvalidRequestException();
		}

	}

	@Override
	public Set<String> getTimezones() {
		String[] allTimezones = TimeZone.getAvailableIDs();
		Set<String> timezones = new TreeSet<String>();

		for (String timezone : allTimezones) {
			if (timezone.matches(TIMEZONE_ID))
				timezones.add(timezone);
		}

		return timezones;
	}

}
