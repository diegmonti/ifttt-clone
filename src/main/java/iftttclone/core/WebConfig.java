package iftttclone.core;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import iftttclone.services.ChannelServiceImpl;
import iftttclone.services.interfaces.ChannelService;

@Configuration
@EnableWebMvc
@ComponentScan("iftttclone.controllers")
public class WebConfig {

	@Bean
	public ChannelService channelService() {
		return new ChannelServiceImpl();
	}

}