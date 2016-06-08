package iftttclone.repositories;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import iftttclone.entities.Channel;
import iftttclone.entities.PublicRecipe;

public interface PublicRecipeRepository extends Repository<PublicRecipe, Long> {

	Iterable<PublicRecipe> findAll(Pageable pageable);
	
	PublicRecipe findOne(Long id);

	PublicRecipe save(PublicRecipe publicRecipe);
	
	void delete(PublicRecipe publicRecipe);
	
	Iterable<PublicRecipe> findAllByTriggerChannel(Pageable pageable, Channel channel);
	
	Iterable<PublicRecipe> findAllByActionChannel(Pageable pageable, Channel channel);
	
	Iterable<PublicRecipe> findAllByTriggerChannelAndActionChannel(Pageable pageable, Channel channel);

}
