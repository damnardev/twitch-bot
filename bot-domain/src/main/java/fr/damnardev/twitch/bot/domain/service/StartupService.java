package fr.damnardev.twitch.bot.domain.service;

import lombok.RequiredArgsConstructor;
import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.port.primary.IStartupService;
import fr.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;

@DomainService
@RequiredArgsConstructor
public class StartupService implements IStartupService {

    private final IAuthenticationRepository authenticationRepository;

    private final IChannelRepository channelRepository;

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
