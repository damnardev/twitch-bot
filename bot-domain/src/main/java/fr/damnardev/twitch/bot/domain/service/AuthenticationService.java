package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.port.primary.IAuthenticationService;
import fr.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IJoinChatRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class AuthenticationService implements IAuthenticationService {

	private final IAuthenticationRepository authenticationRepository;

	private final IJoinChatRepository joinChatRepository;

	@Override
	public void tryRenew() {
		if (this.authenticationRepository.isValid()) {
			return;
		}
		var updated = this.authenticationRepository.renew();
		if (updated) {
			this.joinChatRepository.reconnect();
		}
	}

	@Override
	public boolean isInitialized() {
		return this.authenticationRepository.isInitialized();
	}

}
