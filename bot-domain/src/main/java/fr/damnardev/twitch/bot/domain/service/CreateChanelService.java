package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.primary.ICreateChanelService;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ISaveChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class CreateChanelService implements ICreateChanelService {

	private final IFindChannelRepository findChannelRepository;

	private final ISaveChannelRepository saveChannelRepository;

	@Override
	public ChannelInfo create(ChannelInfo channel) {
		return this.findChannelRepository.find(channel.user()).orElseGet(() -> this.saveChannelRepository.save(channel));
	}

}
