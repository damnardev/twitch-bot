package fr.damnardev.twitch.bot.domain.service.channel;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.ChannelFetchedAllEvent;
import fr.damnardev.twitch.bot.domain.port.primary.channel.FetchAllChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.service.DefaultTryService;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultFetchAllChannelService implements FetchAllChannelService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void process() {
		this.tryService.doTry(this::doInternal);
	}

	private void doInternal() {
		var channels = this.findChannelRepository.findAll();
		var event = ChannelFetchedAllEvent.builder().channels(channels).build();
		this.eventPublisher.publish(event);
	}

}
