package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.event.ChannelCreatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.CreateChannelService;
import fr.damnardev.twitch.bot.domain.port.primary.TryService;
import fr.damnardev.twitch.bot.domain.port.secondary.CreateChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultCreateChannelService implements CreateChannelService {

	private final TryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final CreateChannelRepository createChannelRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void save(CreateChannelForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(CreateChannelForm form) {
		var channelName = form.name();
		var optionalChannel = this.findChannelRepository.findByName(channelName);
		if (optionalChannel.isPresent()) {
			var event = ChannelCreatedEvent.builder().error("Channel already exists").build();
			this.eventPublisher.publish(event);
			return;
		}
		var channel = this.createChannelRepository.save(Channel.builder().name(channelName).build());
		var event = ChannelCreatedEvent.builder().channel(channel).build();
		this.eventPublisher.publish(event);
	}

}
