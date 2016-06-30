package iftttclone.repositories;

import java.util.Set;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.Repository;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.Channel;
import iftttclone.entities.Recipe;
import iftttclone.entities.User;

public interface RecipeRepository extends Repository<Recipe, Long> {

	Iterable<Recipe> findAll();

	Set<Recipe> findRecipeByUser(User user);

	Recipe findRecipeByIdAndUser(Long id, User user);

	Recipe save(Recipe recipe);

	void delete(Recipe recipe);

	@Modifying
	@Transactional
	@Query("UPDATE Recipe SET active = false WHERE user = ?1 AND (trigger IN (FROM Trigger WHERE channel = ?2) OR action IN (FROM Action WHERE channel = ?2))")
	void setAllInactiveByUserAndChannel(User user, Channel channel);

}