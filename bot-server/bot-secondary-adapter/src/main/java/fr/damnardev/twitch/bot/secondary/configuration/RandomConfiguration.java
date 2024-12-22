package fr.damnardev.twitch.bot.secondary.configuration;

import java.security.SecureRandom;
import java.util.Random;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties
public class RandomConfiguration {

	@Bean
	public Random random() {
		return new SecureRandom();
	}

}
