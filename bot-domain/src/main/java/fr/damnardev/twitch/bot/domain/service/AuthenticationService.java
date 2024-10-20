package fr.damnardev.twitch.bot.domain.service;

import lombok.RequiredArgsConstructor;
import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.port.primary.IAuthenticationService;
import fr.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;

@DomainService
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final IAuthenticationRepository authenticationRepository;

    private final IChannelRepository channelRepository;

    @Override
    public void validateToken() {
        var updated = authenticationRepository.validateToken();
        if (updated) {
            channelRepository.reconnect();
        }
    }

    @Override
    public boolean isInitialized() {
        return authenticationRepository.isInitialized();
    }

    @Override
    public void reconnect() {
        channelRepository.reconnect();
    }

}
