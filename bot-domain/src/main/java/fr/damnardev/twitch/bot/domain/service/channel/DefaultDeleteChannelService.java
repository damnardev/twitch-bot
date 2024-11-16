package fr.damnardev.twitch.bot.domain.service.channel;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.event.ChannelDeletedEvent;
import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.channel.DeleteChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.DeleteChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.service.DefaultTryService;
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
	public void process(DeleteChannelForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(DeleteChannelForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.name());
		if (optionalChannel.isEmpty()) {
			throw new BusinessException("Channel not found");
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
