package iftttclone.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import iftttclone.entities.Recipe;
import iftttclone.entities.RecipeLog;

public interface RecipeLogRepository extends Repository<RecipeLog, Long> {

	List<RecipeLog> getRecipeLogByRecipeOrderByTimestampDesc(Recipe recipe, Pageable pageable);

	RecipeLog save(RecipeLog recipeLog);

}
