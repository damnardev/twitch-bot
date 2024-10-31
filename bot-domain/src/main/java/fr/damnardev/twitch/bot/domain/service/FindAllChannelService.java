package fr.damnardev.twitch.bot.domain.service;

import java.util.List;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.primary.IFindAllChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class FindAllChannelService implements IFindAllChannelService {

	private final IFindChannelRepository findChannelRepository;

	@Override
	public List<Channel> findAll() {
		return this.findChannelRepository.findAll();
	}

}
