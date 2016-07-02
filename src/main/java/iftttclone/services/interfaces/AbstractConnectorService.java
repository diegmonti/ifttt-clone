package iftttclone.services.interfaces;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;

public abstract interface AbstractConnectorService {

	@Transactional
	@PreAuthorize("isAuthenticated()")
	public String requestConnection(String path);

	@Transactional
	@PreAuthorize("isAuthenticated()")
	public void validateConnection(String path, String code, String token, String denied);

	@Transactional
	@PreAuthorize("isAuthenticated()")
	public void removeConnection();

}
