package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelEnabledForm;
import fr.damnardev.twitch.bot.domain.port.primary.UpdateEnableChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultUpdateEnableChannelService implements UpdateEnableChannelService {

	private final FindChannelRepository findChannelRepository;

	private final UpdateChannelRepository updateChannelRepository;

	private final ChatRepository chatRepository;

	@Override
	public void updateEnabled(UpdateChannelEnabledForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.name());
		if (optionalChannel.isEmpty()) {
			throw new BusinessException("Channel not found");
		}
		var channel = optionalChannel.get();
		channel = channel.toBuilder().enabled(form.enabled()).build();
		this.updateChannelRepository.update(channel);
		if (channel.enabled()) {
			this.chatRepository.join(channel);
		}
		else {
			this.chatRepository.leave(channel);
		}
	}

}
