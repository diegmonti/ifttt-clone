package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Ingredient;
import iftttclone.entities.Trigger;

public interface IngredientRepository extends Repository<Ingredient, Long> {

	Ingredient getIngredientByNameAndTrigger(String name, Trigger trigger);

	Ingredient save(Ingredient ingredient);

}
