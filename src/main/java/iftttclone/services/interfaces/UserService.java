package iftttclone.services.interfaces;

import java.util.Set;

import org.springframework.security.access.prepost.PreAuthorize;

import iftttclone.entities.User;

public interface UserService {

	@PreAuthorize("isAuthenticated()")
	public User getUser();

	@PreAuthorize("isAnonymous()")
	public void createUser(User user);

	@PreAuthorize("#user.username == authentication.name")
	public void updateUser(User user);

	public Set<String> getTimezones();

}
