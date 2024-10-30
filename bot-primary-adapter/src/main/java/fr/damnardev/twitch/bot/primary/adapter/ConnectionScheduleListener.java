package fr.damnardev.twitch.bot.primary.adapter;

import fr.damnardev.twitch.bot.domain.port.primary.IAuthenticationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ConnectionScheduleListener {

	private final IAuthenticationService authenticationService;

	@Scheduled(cron = "${twitch.scheduled.cron}")
	public void schedule() {
		log.info("Scheduled task");
		if (this.authenticationService.isInitialized()) {
			log.info("Try renew token");
			this.authenticationService.tryRenew();
			log.info("Renew token done");
		}
		log.info("Scheduled task finished");
	}

}
