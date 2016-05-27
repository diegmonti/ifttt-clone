package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Channel;
import iftttclone.entities.Trigger;

public interface TriggerRepository extends Repository<Trigger, Long> {
	
	Trigger getTriggerByMethodAndChannel(String method, Channel channel);
	
	Trigger save(Trigger trigger);

}
