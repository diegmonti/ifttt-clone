package iftttclone.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import iftttclone.entities.SecurityUser;
import iftttclone.entities.User;
import iftttclone.services.interfaces.UserService;

@Component
public class UserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	private UserService userService;

	@Autowired
	public UserDetailsServiceImpl(UserService userService, PasswordEncoder passwordEncoder) {
		// XXX create fake user
		User user = new User();
		user.setEmail("user@example.com");
		user.setUsername("user");
		user.setPassword(passwordEncoder.encode("pass"));
		user.setTimezone("UTC");
		userService.createOrUpdateUser(user);
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.getUser(username);
		if (user == null) {
			throw new UsernameNotFoundException("Username " + username + " not found");
		}
		return new SecurityUser(user);
	}

}
