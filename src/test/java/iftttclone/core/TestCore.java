package iftttclone.core;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import iftttclone.core.config.RootConfig;

@Order
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {RootConfig.class})
@WebAppConfiguration
public class TestCore {
	@Autowired
	private TestRecipesCreator testRecipesCreator;
	
	@Test
	public void doTests() throws InterruptedException{
		testRecipesCreator.createTests();
		
		Thread.sleep(180000);	// three seconds, wait for scheduler
	}

}
