package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Trigger;
import iftttclone.entities.TriggerField;

public interface TriggerFieldRepository extends Repository<TriggerField, Long> {

	TriggerField getTriggerFieldByParameterAndTrigger(String parameter, Trigger trigger);

	TriggerField save(TriggerField triggerField);

}
