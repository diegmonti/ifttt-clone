package iftttclone.repositories;

import java.util.Collection;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;

public interface ActionRepository extends Repository<Action, Long> {

	Collection<Action> getActionsByChannel(Channel channel);

	Action getActionByMethodAndChannel(String method, Channel channel);

	Action save(Action action);

}
