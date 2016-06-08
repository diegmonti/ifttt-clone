package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.User;

public interface UserRepository extends Repository<User, Long> {
	
	User getUserByUsername(String username);
	
	User findOne(Long id);
	
	User save(User user);
	
	void delete(User user);
	
}
