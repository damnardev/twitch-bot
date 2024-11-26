package fr.damnardev.twitch.bot.primary.adapter;

import fr.damnardev.twitch.bot.port.primary.StartupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationStartupListener implements ApplicationRunner {

	private final ThreadPoolTaskExecutor executor;

	private final StartupService startupService;

	@Override
	public void run(ApplicationArguments args) {
		this.executor.execute(this::doInternal);
	}

	private void doInternal() {
		log.info("Starting bot");
		this.startupService.run();
		log.info("Bot started");
	}

}
