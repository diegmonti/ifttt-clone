package iftttclone.services;

import java.util.Set;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.PublicRecipe;
import iftttclone.services.interfaces.PublicRecipeService;

@Component
@Transactional
public class PublicRecipeServiceImpl implements PublicRecipeService {

	@Override
	public Set<PublicRecipe> getPublicRecipes(Integer page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PublicRecipe> getPublicRecipesByName(String name, Integer page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublicRecipe getPublicRecipe(Long publicRecipeId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublicRecipe addPublicRecipe(PublicRecipe publicRecipe) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PublicRecipe updatePublicRecipe(PublicRecipe stub) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deletePublicRecipe(Long publicRecipeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addToFavorite(Long publicRecipeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeFromFavorite(Long publicRecipeId) {
		// TODO Auto-generated method stub

	}

	@Override
	public Set<PublicRecipe> getFavoritePublicRecipes(Integer page) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<PublicRecipe> getPublishedPublicRecipes(Integer page) {
		// TODO Auto-generated method stub
		return null;
	}

}
