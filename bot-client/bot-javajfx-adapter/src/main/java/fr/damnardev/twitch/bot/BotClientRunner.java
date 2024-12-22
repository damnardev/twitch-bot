package fr.damnardev.twitch.bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.FullyQualifiedAnnotationBeanNameGenerator;

@ComponentScan(basePackages = "fr.damnardev.twitch.bot", includeFilters = { @ComponentScan.Filter(type = FilterType.ANNOTATION, value = DomainService.class) }, nameGenerator = FullyQualifiedAnnotationBeanNameGenerator.class)
@SpringBootApplication
public class BotClientRunner {

	public static void main(String[] args) {
		SpringApplication.run(BotClientRunner.class, args);
	}

}
