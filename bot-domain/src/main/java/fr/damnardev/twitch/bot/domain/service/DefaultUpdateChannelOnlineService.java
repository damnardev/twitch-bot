package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelOnlineForm;
import fr.damnardev.twitch.bot.domain.port.primary.UpdateChannelOnlineService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultUpdateChannelOnlineService implements UpdateChannelOnlineService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final UpdateChannelRepository updateChannelRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void process(UpdateChannelOnlineForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(UpdateChannelOnlineForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.name());
		if (optionalChannel.isEmpty()) {
			var event = ChannelUpdatedEvent.builder().error("Channel not found").build();
			this.eventPublisher.publish(event);
			return;
		}
		var channel = optionalChannel.get();
		channel = channel.toBuilder().online(form.online()).build();
		this.updateChannelRepository.update(channel);
		var event = ChannelUpdatedEvent.builder().channel(channel).build();
		this.eventPublisher.publish(event);
	}

}
