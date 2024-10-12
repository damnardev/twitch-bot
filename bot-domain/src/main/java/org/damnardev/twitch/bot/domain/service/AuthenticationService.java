package org.damnardev.twitch.bot.domain.service;

import lombok.RequiredArgsConstructor;
import org.damnardev.twitch.bot.domain.DomainService;
import org.damnardev.twitch.bot.domain.port.primary.IAuthenticationService;
import org.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import org.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;

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
