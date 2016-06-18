package iftttclone.services;

import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import iftttclone.entities.PublicRecipe;
import iftttclone.entities.User;
import iftttclone.exceptions.InvalidRequestException;
import iftttclone.exceptions.ResourceNotFoundException;
import iftttclone.repositories.PublicRecipeRepository;
import iftttclone.repositories.UserRepository;
import iftttclone.services.interfaces.PublicRecipeService;
import iftttclone.services.interfaces.UserService;

@Component
@Transactional
public class PublicRecipeServiceImpl implements PublicRecipeService {
	private static final Integer PAGE_SIZE = 25;

	@Autowired
	private PublicRecipeRepository publicRecipeRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private UserService userService;

	@Override
	public List<PublicRecipe> getPublicRecipes(Integer page) {
		if (page < 0) {
			throw new InvalidRequestException("Page cannot be negative");
		}
		Pageable pageable = new PageRequest(page, PAGE_SIZE);
		return publicRecipeRepository.findAll(pageable);
	}

	@Override
	public List<PublicRecipe> getPublicRecipesByTitle(String title, Integer page) {
		if (page < 0) {
			throw new InvalidRequestException("Page cannot be negative");
		}
		Pageable pageable = new PageRequest(page, PAGE_SIZE);
		return publicRecipeRepository.findAllByTitleContaining(title, pageable);
	}

	@Override
	public PublicRecipe getPublicRecipe(Long publicRecipeId) {
		PublicRecipe publicRecipe = publicRecipeRepository.findOne(publicRecipeId);
		if (publicRecipe == null) {
			throw new ResourceNotFoundException();
		}
		return publicRecipe;
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
		User user = userService.getUser();
		PublicRecipe publicRecipe = publicRecipeRepository.findPublicRecipeByIdAndUser(publicRecipeId, user);
		if (publicRecipe == null) {
			throw new SecurityException();
		}
		publicRecipeRepository.delete(publicRecipe);
	}

	@Override
	public void addToFavorite(Long publicRecipeId) {
		User user = userService.getUser();
		PublicRecipe publicRecipe = publicRecipeRepository.findOne(publicRecipeId);
		if (publicRecipe == null) {
			throw new ResourceNotFoundException();
		}
		if (!user.getFavoritePublicRecipes().contains(publicRecipe)) {
			publicRecipe.setFavorites(publicRecipe.getFavorites() + 1);
			publicRecipeRepository.save(publicRecipe);
			user.getFavoritePublicRecipes().add(publicRecipe);
			userRepository.save(user);
		} else {
			throw new InvalidRequestException("The public recipe is already favorite");
		}
	}

	@Override
	public void removeFromFavorite(Long publicRecipeId) {
		User user = userService.getUser();
		PublicRecipe publicRecipe = publicRecipeRepository.findOne(publicRecipeId);
		if (publicRecipe == null) {
			throw new ResourceNotFoundException();
		}
		if (user.getFavoritePublicRecipes().contains(publicRecipe)) {
			publicRecipe.setFavorites(publicRecipe.getFavorites() - 1);
			publicRecipeRepository.save(publicRecipe);
			user.getFavoritePublicRecipes().remove(publicRecipe);
			userRepository.save(user);
		} else {
			throw new InvalidRequestException("The public recipe is already not favorite");
		}
	}

	@Override
	public Set<PublicRecipe> getFavoritePublicRecipes() {
		User user = userService.getUser();
		return user.getFavoritePublicRecipes();
	}

	@Override
	public List<PublicRecipe> getPublishedPublicRecipes(Integer page) {
		if (page < 0) {
			throw new InvalidRequestException("Page cannot be negative");
		}
		User user = userService.getUser();
		Pageable pageable = new PageRequest(page, PAGE_SIZE);
		return publicRecipeRepository.findAllByUser(user, pageable);
	}

}
