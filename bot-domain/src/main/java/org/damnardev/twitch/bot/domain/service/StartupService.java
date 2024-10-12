package org.damnardev.twitch.bot.domain.service;

import lombok.RequiredArgsConstructor;
import org.damnardev.twitch.bot.domain.DomainService;
import org.damnardev.twitch.bot.domain.model.event.RaidEvent;
import org.damnardev.twitch.bot.domain.port.primary.IRaidEventService;
import org.damnardev.twitch.bot.domain.port.primary.IStartupService;
import org.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import org.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;

@DomainService
@RequiredArgsConstructor
public class StartupService implements IStartupService {

    private final IAuthenticationRepository authenticationRepository;

    private final IChannelRepository channelRepository;

    private final IRaidEventService raidEventService;

    @Override
    public void run() {
        var updated = authenticationRepository.validateToken();
        if (!updated) {
            System.exit(-1);
        }
        channelRepository.joinAllChannel();
        channelRepository.computeStatus();
        channelRepository.reconnect();
    }

}
