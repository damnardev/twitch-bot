package fr.damnardev.twitch.bot.primary.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.damnardev.twitch.bot.domain.port.primary.IAuthenticationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ConnectionScheduleListener {

    private final IAuthenticationService handler;

    @Scheduled(cron = "0 0/10 * * * *")
    public void schedule() {
        log.info("Scheduled task");
        if (handler.isInitialized()) {
            log.info("Validating token");
            handler.validateToken();
            log.info("Token validated");
        }
        log.info("Scheduled task finished");
    }

}
