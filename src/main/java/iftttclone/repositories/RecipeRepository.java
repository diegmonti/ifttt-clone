package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Recipe;

public interface RecipeRepository extends Repository<Recipe, Long> {

	Iterable<Recipe> findAll();

	Recipe save(Recipe recipe);

}