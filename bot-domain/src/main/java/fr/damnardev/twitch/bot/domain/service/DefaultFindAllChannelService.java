package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.ChannelFindEvent;
import fr.damnardev.twitch.bot.domain.port.primary.FindAllChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultFindAllChannelService implements FindAllChannelService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void findAll() {
		this.tryService.doTry(this::doInternal);
	}

	private void doInternal() {
		var channels = this.findChannelRepository.findAll();
		var event = ChannelFindEvent.builder().channels(channels).build();
		this.eventPublisher.publish(event);
	}

}
