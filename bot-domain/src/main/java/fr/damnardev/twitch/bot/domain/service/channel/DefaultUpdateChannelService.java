package fr.damnardev.twitch.bot.domain.service.channel;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.event.ChannelUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.channel.UpdateChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.StreamRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.UpdateChannelRepository;
import fr.damnardev.twitch.bot.domain.service.DefaultTryService;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultUpdateChannelService implements UpdateChannelService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final UpdateChannelRepository updateChannelRepository;

	private final ChatRepository chatRepository;

	private final StreamRepository streamRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void process(UpdateChannelForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(UpdateChannelForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.name());
		if (optionalChannel.isEmpty()) {
			throw new BusinessException("Channel not found");
		}
		var channel = optionalChannel.get();
		if (form.enabled() != null) {
			channel = channel.toBuilder().enabled(form.enabled()).online(false).build();
			if (channel.enabled()) {
				channel = this.streamRepository.computeOnline(channel);
				this.chatRepository.join(channel);
			}
			else {
				this.chatRepository.leave(channel);
			}
		}
		if (form.online() != null) {
			channel = channel.toBuilder().online(form.online()).build();
		}
		this.updateChannelRepository.update(channel);
		var event = ChannelUpdatedEvent.builder().channel(channel).build();
		this.eventPublisher.publish(event);
	}

}
