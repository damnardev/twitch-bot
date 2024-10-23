package fr.damnardev.twitch.bot.primary.adapter;

import fr.damnardev.twitch.bot.domain.port.primary.IStartupService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationStartupListener implements ApplicationRunner {

    private final IStartupService startupService;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting bot");
        startupService.run();
        log.info("Bot started");
    }

}
