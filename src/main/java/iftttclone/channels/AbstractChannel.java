package iftttclone.channels;

import java.util.Date;

import iftttclone.entities.ChannelConnector;
import iftttclone.entities.User;
//import iftttclone.repositories.RecipeRepository;

/**
 * This class contains the information needed by the channels to execute triggers and actions.
 */
public abstract class AbstractChannel {
	/**
	 * The information needed to interact with an external service on behalf of
	 * the current user.
	 */
	private ChannelConnector channelConnector;
	/**
	 * The last time the recipe triggered an action.
	 */
	private Date lastRun;
	/**
	 * The current user, useful to get the time zone.
	 */
	private User user;
	/**
	 * The id of the recipe, used in Twitter.
	 */
	//private Long recipeId;
	/**
	 * The recipe repository, used in Twitter.
	 */
	//private RecipeRepository recipeRepository;

	public ChannelConnector getChannelConnector() {
		return channelConnector;
	}

	public void setChannelConnector(ChannelConnector channelConnector) {
		this.channelConnector = channelConnector;
	}

	public Date getLastRun() {
		return lastRun;
	}

	public void setLastRun(Date lastRun) {
		this.lastRun = lastRun;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	/*public Long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}

	public RecipeRepository getRecipeRepository() {
		return recipeRepository;
	}

	public void setRecipeRepository(RecipeRepository recipeRepository) {
		this.recipeRepository = recipeRepository;
	}*/

}
