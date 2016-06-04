package iftttclone.services.interfaces;

import iftttclone.entities.User;

public interface UserService {

	public Long createOrUpdateUser(User user);

	public User getUser(Long userId);

	public User getUser(String username);
}
