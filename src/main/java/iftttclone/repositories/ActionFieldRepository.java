package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Action;
import iftttclone.entities.ActionField;

public interface ActionFieldRepository extends Repository<ActionField, Long> {
	
	ActionField getActionFieldByParameterAndAction(String parameter, Action action);

	ActionField save(ActionField actionField);

}
