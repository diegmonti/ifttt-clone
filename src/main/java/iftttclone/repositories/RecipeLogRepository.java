package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.RecipeLog;

public interface RecipeLogRepository extends Repository<RecipeLog, Long> {

	RecipeLog save(RecipeLog recipeLog);

}
