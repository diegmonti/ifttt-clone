package iftttclone.services.interfaces;

import iftttclone.entities.User;


public interface UserService {

	public Long createOrUpdateUser(User user);
	public boolean authUser(String username, String password);
	public User getUser(Long userId);
	public User getUser(String username);
}
