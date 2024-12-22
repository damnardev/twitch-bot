package fr.damnardev.twitch.bot.database.configuration;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = "fr.damnardev.twitch.bot.database.repository")
@EnableTransactionManagement
@EntityScan("fr.damnardev.twitch.bot.database.entity")
public class DatabaseConfiguration {

}
