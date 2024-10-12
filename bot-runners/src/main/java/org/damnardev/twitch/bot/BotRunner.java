package org.damnardev.twitch.bot;

import org.damnardev.twitch.bot.domain.DomainService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@ComponentScan(basePackages = "org.damnardev.twitch.bot",
               includeFilters = {@ComponentScan.Filter(type = FilterType.ANNOTATION,
                                                       value = DomainService.class)},
               nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@SpringBootApplication
public class BotRunner {

    public static void main(String[] args) {
        System.setProperty("java.awt.headless", "false");
        SpringApplication.run(BotRunner.class, args);
    }

}
