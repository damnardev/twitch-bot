package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.port.primary.IAuthenticationService;
import fr.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IChatRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

    private final IAuthenticationRepository authenticationRepository;

    private final IChatRepository chatRepository;

    @Override
    public void tryRenew() {
        if (authenticationRepository.isValid()) {
            return;
        }
        var updated = authenticationRepository.renew();
        if (updated) {
            chatRepository.reconnect();
        }
    }

    @Override
    public boolean isInitialized() {
        return authenticationRepository.isInitialized();
    }

}
