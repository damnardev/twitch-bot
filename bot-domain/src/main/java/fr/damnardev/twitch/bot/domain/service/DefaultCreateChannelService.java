package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.CreateChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.CreateChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultCreateChannelService implements CreateChannelService {

	private final FindChannelRepository findChannelRepository;

	private final CreateChannelRepository createChannelRepository;

	@Override
	public Channel save(CreateChannelForm form) {
		this.findChannelRepository.findByName(form.name()).ifPresent((_) -> {
			throw new BusinessException("Channel already exists");
		});
		return this.createChannelRepository.save(form);
	}

}
