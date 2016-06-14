package iftttclone.repositories;

import java.util.Set;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Recipe;
import iftttclone.entities.User;

public interface RecipeRepository extends Repository<Recipe, Long> {

	Iterable<Recipe> findAll();

	Set<Recipe> findRecipeByUser(User user);

	Recipe findRecipeByIdAndUser(Long id, User user);

	Recipe save(Recipe recipe);

	void delete(Recipe recipe);

}