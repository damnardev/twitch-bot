package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.domain.port.primary.ChannelMessageEventService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultChannelMessageEventService implements ChannelMessageEventService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void process(ChannelMessageEventForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(ChannelMessageEventForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.channelName());
		if (optionalChannel.isEmpty()) {
			throw new BusinessException("Channel not found");
		}
	}

}
