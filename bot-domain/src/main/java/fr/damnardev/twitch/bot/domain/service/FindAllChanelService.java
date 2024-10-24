package fr.damnardev.twitch.bot.domain.service;

import java.util.List;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.primary.IFindAllChanelService;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindAllChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class FindAllChanelService implements IFindAllChanelService {

	private final IFindAllChannelRepository findAllChannelRepository;

	@Override
	public List<ChannelInfo> findAll() {
		return this.findAllChannelRepository.findAll();
	}

}
