package iftttclone.services.interfaces;

import iftttclone.entities.User;

public interface UserService {

	public Long createUser(User user); // user.id = null
	public boolean authUser(String username, String password);
	public void updateUser(User user); // user.id != null

	public void getUser(String username);
	public void getUser(Long userId);
}
