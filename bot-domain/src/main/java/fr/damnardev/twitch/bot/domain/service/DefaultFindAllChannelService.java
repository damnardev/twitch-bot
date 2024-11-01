package fr.damnardev.twitch.bot.domain.service;

import java.util.List;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.primary.FindAllChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultFindAllChannelService implements FindAllChannelService {

	private final FindChannelRepository findChannelRepository;

	@Override
	public List<Channel> findAll() {
		return this.findChannelRepository.findAll();
	}

}
