package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.ApplicationStartedEvent;
import fr.damnardev.twitch.bot.domain.port.primary.StartupService;
import fr.damnardev.twitch.bot.domain.port.secondary.AuthenticationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.StreamRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultStartupService implements StartupService {

	private final AuthenticationRepository authenticationRepository;

	private final FindChannelRepository findChannelRepository;

	private final ChatRepository chatRepository;

	private final StreamRepository streamRepository;

	private final EventPublisher eventPublisher;

	private final UpdateChannelRepository updateChannelRepository;

	@Override
	public void run() {
		var generated = this.authenticationRepository.renew();
		if (!generated) {
			System.exit(-1);
		}
		var channels = this.findChannelRepository.findAllEnabled();
		this.chatRepository.joinAll(channels);
		this.chatRepository.reconnect();
		channels = this.streamRepository.computeOnline(channels);
		this.updateChannelRepository.updateAll(channels);
		this.eventPublisher.publish(ApplicationStartedEvent.builder().build());
	}

}
