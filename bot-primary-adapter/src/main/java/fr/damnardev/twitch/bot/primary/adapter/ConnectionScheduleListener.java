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

    @Scheduled(cron = "0 0/10 * * * *")
    public void schedule() {
        log.info("Scheduled task");
        if (authenticationService.isInitialized()) {
            log.info("Try renew token");
            authenticationService.tryRenew();
            log.info("Renew token done");
        }
        log.info("Scheduled task finished");
    }

}
