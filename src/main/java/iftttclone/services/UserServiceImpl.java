package iftttclone.services;

import org.springframework.beans.factory.annotation.Autowired;

import iftttclone.entities.User;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.UserService;


public class UserServiceImpl implements UserService{

	@Autowired
	UserRepository userRepository;
	
	
	@Override
	public Long createOrUpdateUser(User user) {
		return userRepository.save(user).getId();
	}

	@Override
	public boolean authUser(String username, String password) {
		User user = userRepository.getUserByUsername(username);
		if(user == null) return false;
		return user.getPassword().equals(password);
		/*
		 * TODO: maybe compute sha1 + salt of the password
		 * Using apache common codec library:
		 * DigestUtils.sha1Hex("aff")
		 */
	}

	@Override
	public User getUser(Long userId) {
		return userRepository.getUserById(userId);
	}

	@Override
	public User getUser(String username) {
		return userRepository.getUserByUsername(username);
	}
}
