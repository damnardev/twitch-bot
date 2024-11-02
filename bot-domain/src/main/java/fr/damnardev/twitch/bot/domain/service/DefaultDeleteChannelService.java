package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.ChannelDeletedEvent;
import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.DeleteChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.DeleteChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultDeleteChannelService implements DeleteChannelService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final DeleteChannelRepository deleteChannelRepository;

	private final ChatRepository chatRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void delete(DeleteChannelForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(DeleteChannelForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.name());
		if (optionalChannel.isEmpty()) {
			var event = ChannelDeletedEvent.builder().error("Channel not found").build();
			this.eventPublisher.publish(event);
			return;
		}
		var channel = optionalChannel.get();
		if (channel.enabled()) {
			this.chatRepository.leave(channel);
		}
		this.deleteChannelRepository.delete(channel);
		var event = ChannelDeletedEvent.builder().channel(channel).build();
		this.eventPublisher.publish(event);
	}

}
