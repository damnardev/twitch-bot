package org.damnardev.twitch.bot.secondary.configuration;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.security.SecureRandom;
import java.util.Random;

@Configuration
@EnableConfigurationProperties
public class RandomConfiguration {

    @Bean
    public Random random() {
        return new SecureRandom();
    }

}
