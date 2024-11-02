package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelEnabledForm;
import fr.damnardev.twitch.bot.domain.port.primary.UpdateChannelEnableService;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.StreamRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultUpdateChannelEnableService implements UpdateChannelEnableService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final UpdateChannelRepository updateChannelRepository;

	private final ChatRepository chatRepository;

	private final StreamRepository streamRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void updateEnabled(UpdateChannelEnabledForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(UpdateChannelEnabledForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.name());
		if (optionalChannel.isEmpty()) {
			var event = ChannelUpdatedEvent.builder().error("Channel not found").build();
			this.eventPublisher.publish(event);
			return;
		}
		var channel = optionalChannel.get();
		channel = channel.toBuilder().enabled(form.enabled()).online(false).build();
		this.updateChannelRepository.update(channel);
		if (channel.enabled()) {
			channel = this.streamRepository.compute(channel);
			this.chatRepository.join(channel);
		}
		else {
			this.chatRepository.leave(channel);
		}
		var event = ChannelUpdatedEvent.builder().channel(channel).build();
		this.eventPublisher.publish(event);
	}

}
