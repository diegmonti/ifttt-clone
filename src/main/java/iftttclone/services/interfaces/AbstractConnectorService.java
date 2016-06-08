package iftttclone.services.interfaces;

import java.io.IOException;

import org.springframework.security.access.prepost.PreAuthorize;

public abstract interface AbstractConnectorService {

	@PreAuthorize("isAuthenticated()")
	public String requestConnection(String path);

	@PreAuthorize("isAuthenticated()")
	public void validateConnection(String path, String code, String token) throws IOException;

}
