package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.FatalException;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.primary.IDeleteChanelService;
import fr.damnardev.twitch.bot.domain.port.secondary.IDeleteChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ILeaveChatRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DeleteChanelService implements IDeleteChanelService {

	private final IFindChannelRepository findChannelRepository;

	private final IDeleteChannelRepository deleteChannelRepository;

	private final ILeaveChatRepository leaveChatRepository;

	public void delete(ChannelInfo channelInf) {
		if (this.findChannelRepository.find(channelInf.user()).isEmpty()) {
			throw new FatalException("channelInfo not found.");
		}
		this.leaveChatRepository.leaveChannel(channelInf);
		this.deleteChannelRepository.delete(channelInf);
	}
}
