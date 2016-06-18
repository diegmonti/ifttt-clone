package iftttclone.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import iftttclone.entities.PublicRecipe;
import iftttclone.entities.User;

public interface PublicRecipeRepository extends Repository<PublicRecipe, Long> {

	List<PublicRecipe> findAll(Pageable pageable);

	List<PublicRecipe> findAllByTitleContaining(String title, Pageable pageable);
	
	List<PublicRecipe> findAllByUser(User user, Pageable pageable);

	PublicRecipe findOne(Long id);
	
	PublicRecipe findPublicRecipeByIdAndUser(Long id, User user);

	PublicRecipe save(PublicRecipe publicRecipe);

	void delete(PublicRecipe publicRecipe);

}
