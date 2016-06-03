package iftttclone.core;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// TODO database authentication
		auth.inMemoryAuthentication().withUser("user").password("pass").roles("USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// TODO check matchers correctness
		http.httpBasic().and().authorizeRequests()
				.antMatchers("/", "/css/**", "/fonts/**", "/img/**", "/js/**", "/partials/**", "/index.html",
						"/api/channels", "/api/user", "/api/recipes")
				.permitAll().anyRequest().authenticated().and().addFilterAfter(new CsrfHeaderFilter(), CsrfFilter.class)
				.csrf().csrfTokenRepository(csrfTokenRepository())
				.and().logout().logoutUrl("/api/user/logout");
	}

	private CsrfTokenRepository csrfTokenRepository() {
		HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
		repository.setHeaderName("X-XSRF-TOKEN");
		return repository;
	}

}
