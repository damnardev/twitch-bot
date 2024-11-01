package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.DeleteChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.DeleteChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultDeleteChannelService implements DeleteChannelService {

	private final FindChannelRepository findChannelRepository;

	private final DeleteChannelRepository deleteChannelRepository;

	private final ChatRepository chatRepository;

	@Override
	public void delete(DeleteChannelForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.name());
		if (optionalChannel.isEmpty()) {
			throw new BusinessException("Channel not found");
		}
		var channel = optionalChannel.get();
		if (channel.enabled()) {
			this.chatRepository.leave(channel);
		}
		this.deleteChannelRepository.delete(channel);
	}

}
