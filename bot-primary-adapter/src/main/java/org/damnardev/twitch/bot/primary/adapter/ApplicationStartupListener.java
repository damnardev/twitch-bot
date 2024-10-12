package org.damnardev.twitch.bot.primary.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.damnardev.twitch.bot.domain.port.primary.IStartupService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationStartupListener implements ApplicationRunner {

    private final IStartupService handler;

    @Override
    public void run(ApplicationArguments args) {
        log.info("Starting bot");
        handler.run();
        log.info("Bot started");
    }

}
