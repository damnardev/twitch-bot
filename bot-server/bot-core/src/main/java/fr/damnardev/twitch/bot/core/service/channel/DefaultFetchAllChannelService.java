package fr.damnardev.twitch.bot.core.service.channel;

import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.core.service.DefaultTryService;
import fr.damnardev.twitch.bot.model.event.ChannelFetchedAllEvent;
import fr.damnardev.twitch.bot.port.primary.channel.FetchAllChannelService;
import fr.damnardev.twitch.bot.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.port.secondary.channel.FindChannelRepository;
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
