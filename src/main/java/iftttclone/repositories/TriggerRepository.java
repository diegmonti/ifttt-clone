package iftttclone.repositories;

import java.util.Collection;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Channel;
import iftttclone.entities.Trigger;

public interface TriggerRepository extends Repository<Trigger, Long> {
	
	Collection<Trigger> getTriggersByChannel(Channel channel);
	
	Trigger getTriggerByMethodAndChannel(String method, Channel channel);
	
	Trigger save(Trigger trigger);

}
