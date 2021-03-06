package iftttclone.core;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import iftttclone.entities.User;
import iftttclone.repositories.UserRepository;

@Component
public class Utils {
	@Autowired
	private UserRepository userRepository;

	/**
	 * This method, given a request, returns the full URL of the request.
	 * @param req HTTP request
	 * @return URL
	 */
	public String getURL(HttpServletRequest req) {
		String scheme = req.getScheme();
		String serverName = req.getServerName();
		int serverPort = req.getServerPort();
		String contextPath = req.getContextPath();

		StringBuilder url = new StringBuilder();
		url.append(scheme).append("://").append(serverName);

		if (serverPort != 80 && serverPort != 443) {
			url.append(":").append(serverPort);
		}

		url.append(contextPath);

		return url.toString();
	}

	/**
	 * This method returns the current authenticated user or null if the user is
	 * anonymous.
	 * @return the current user or null
	 */
	public User getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication == null || authentication instanceof AnonymousAuthenticationToken) {
			return null;
		}

		return userRepository.getUserByUsername(authentication.getName());
	}

}
