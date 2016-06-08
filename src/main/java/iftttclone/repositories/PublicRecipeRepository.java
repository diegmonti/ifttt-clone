package iftttclone.repositories;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.Repository;

import iftttclone.entities.Channel;
import iftttclone.entities.PublicRecipe;

public interface PublicRecipeRepository extends Repository<PublicRecipe, Long> {

	List<PublicRecipe> findAll(Pageable pageable);
	
	PublicRecipe findOne(Long id);

	PublicRecipe save(PublicRecipe publicRecipe);
	
	void delete(PublicRecipe publicRecipe);
	
	List<PublicRecipe> findAllByTriggerChannel(Pageable pageable, Channel channel);
	
	List<PublicRecipe> findAllByActionChannel(Pageable pageable, Channel channel);
	
	List<PublicRecipe> findAllByTriggerChannelAndActionChannel(Pageable pageable, Channel channel);

}
