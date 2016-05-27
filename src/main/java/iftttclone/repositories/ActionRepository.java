package iftttclone.repositories;

import org.springframework.data.repository.Repository;

import iftttclone.entities.Action;
import iftttclone.entities.Channel;

public interface ActionRepository extends Repository<Action, Long> {

	Action getActionByMethodAndChannel(String method, Channel channel);

	Action save(Action action);

}
