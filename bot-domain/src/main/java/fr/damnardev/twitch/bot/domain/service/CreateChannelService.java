package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.ICreateChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.ICreateChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class CreateChannelService implements ICreateChannelService {

	private final IFindChannelRepository findChannelRepository;

	private final ICreateChannelRepository createChannelRepository;

	@Override
	public Channel save(CreateChannelForm form) {
		this.findChannelRepository.findByName(form.name()).ifPresent((_) -> {
			throw new BusinessException("Channel already exists");
		});
		return this.createChannelRepository.save(form);
	}

}
