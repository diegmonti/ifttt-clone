package iftttclone.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;

import iftttclone.entities.Channel;
import iftttclone.entities.PublicRecipe;
import iftttclone.entities.User;

public interface PublicRecipeRepository extends Repository<PublicRecipe, Long> {

	List<PublicRecipe> findAll(Pageable pageable);

	List<PublicRecipe> findAllByTitleContainingOrDescriptionContaining(String title, String description, Pageable pageable);
	
	List<PublicRecipe> findAllByUser(User user, Pageable pageable);
	
	@Query("FROM PublicRecipe WHERE (trigger IN (FROM Trigger WHERE channel = ?1) OR action IN (FROM Action WHERE channel = ?1)) ORDER BY favorites DESC")
	List<PublicRecipe> findAllByChannel(Channel channel, Pageable pageable);

	PublicRecipe findOne(Long id);
	
	PublicRecipe findPublicRecipeByIdAndUser(Long id, User user);

	PublicRecipe save(PublicRecipe publicRecipe);

	void delete(PublicRecipe publicRecipe);

}
