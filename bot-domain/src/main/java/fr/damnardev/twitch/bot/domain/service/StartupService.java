package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.port.primary.IStartupService;
import fr.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IJoinChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IStreamRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class StartupService implements IStartupService {

	private final IAuthenticationRepository authenticationRepository;

	private final IJoinChatRepository joinChatRepository;

	private final IStreamRepository streamRepository;

	@Override
	public void run() {
		var generated = this.authenticationRepository.renew();
		if (!generated) {
			System.exit(-1);
		}
		this.joinChatRepository.joinAllChannel();
		this.joinChatRepository.reconnect();
		this.streamRepository.computeStatus();
	}

}
