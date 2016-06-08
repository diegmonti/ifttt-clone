package iftttclone.services;

import org.springframework.beans.factory.annotation.Autowired;

import iftttclone.entities.User;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.UserService;

public class UserServiceImpl implements UserService {
	@Autowired
	UserRepository userRepository;

	@Override
	public Long createOrUpdateUser(User user) {
		return userRepository.save(user).getId();
	}

	@Override
	public User getUser(Long userId) {
		return userRepository.findOne(userId);
	}

	@Override
	public User getUser(String username) {
		return userRepository.getUserByUsername(username);
	}
}
