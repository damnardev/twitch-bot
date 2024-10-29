package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.port.primary.IStartupService;
import fr.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IStreamRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class StartupService implements IStartupService {

	private final IAuthenticationRepository authenticationRepository;

	private final IChannelRepository channelRepository;

	private final IChatRepository chatRepository;

	private final IStreamRepository streamRepository;

	@Override
	public void run() {
		var generated = this.authenticationRepository.renew();
		if (!generated) {
			System.exit(-1);
		}
		var channels = this.channelRepository.findAllEnabled();
		this.chatRepository.joinAll(channels);
		this.chatRepository.reconnect();
		this.streamRepository.computeAll(channels);
	}

}
