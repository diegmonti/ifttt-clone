package iftttclone.core;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.mariadb.jdbc.Driver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.PlatformTransactionManager;

import com.google.api.client.util.store.DataStoreFactory;

import iftttclone.services.ChannelServiceImpl;
import iftttclone.services.GmailConnectorServiceImpl;
import iftttclone.services.GoogleCalendarConnectorServiceImpl;
import iftttclone.services.UserServiceImpl;
import iftttclone.services.interfaces.ChannelService;
import iftttclone.services.interfaces.GmailConnectorService;
import iftttclone.services.interfaces.GoogleCalendarConnectorService;
import iftttclone.services.interfaces.UserService;

@Configuration
@EnableScheduling
@ComponentScan(basePackages = {"iftttclone.core", "iftttclone.services"})
@EnableJpaRepositories("iftttclone.repositories")
@PropertySource(value = { "classpath:application.properties" })
public class RootConfig {
	@Autowired
	private Environment env;

	@Bean
	public DataSource dataSource() {
		BasicDataSource ds = new BasicDataSource();
		ds.setDriverClassName(Driver.class.getName());
		ds.setUrl(env.getProperty("jdbc.url"));
		ds.setUsername(env.getProperty("jdbc.username"));
		ds.setPassword(env.getProperty("jdbc.password"));
		return ds;
	}

	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
		HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		vendorAdapter.setDatabase(Database.MYSQL);

		LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
		factory.setJpaVendorAdapter(vendorAdapter);
		factory.setPackagesToScan("iftttclone.entities");
		factory.setDataSource(dataSource);

		Properties properties = new Properties();
		properties.put("hibernate.hbm2ddl.auto", env.getProperty("hibernate.hbm2ddl.auto"));
		properties.put("hibernate.show_sql", env.getProperty("hibernate.show_sql"));
		factory.setJpaProperties(properties);
		return factory;
	}

	@Bean
	public PlatformTransactionManager transactionManager(EntityManagerFactory factory) {
		JpaTransactionManager txManager = new JpaTransactionManager();
		txManager.setEntityManagerFactory(factory);
		return txManager;
	}
	
	@Bean DataStoreFactory dataStoreFactory(){
		// TODO: implement this
		return null;
	}
	
	
	// Services
	
	@Bean
	public ChannelService channelService() {
		return new ChannelServiceImpl();
	}

	@Bean
	public UserService userService() {
		return new UserServiceImpl();
	}

	@Bean
	public GmailConnectorService gmailConnectorService() throws GeneralSecurityException, IOException {
		return new GmailConnectorServiceImpl();
	}

	@Bean
	public GoogleCalendarConnectorService googleCalendarConnectorService()
			throws GeneralSecurityException, IOException {
		return new GoogleCalendarConnectorServiceImpl();
	}
}