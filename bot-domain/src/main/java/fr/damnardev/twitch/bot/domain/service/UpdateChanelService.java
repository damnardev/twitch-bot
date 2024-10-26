package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.FatalException;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.primary.IUpdateChanelService;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IJoinChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ILeaveChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IStreamRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IUpdateChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class UpdateChanelService implements IUpdateChanelService {

	private final IFindChannelRepository findChannelRepository;

	private final IUpdateChannelRepository updateChannelRepository;

	private final IJoinChatRepository joinChatRepository;

	private final ILeaveChatRepository leaveChatRepository;

	private final IStreamRepository streamRepository;

	@Override
	public void update(ChannelInfo channel) {
		if (this.findChannelRepository.find(channel.user()).isEmpty()) {
			throw new FatalException("channel not found.");
		}
		this.updateChannelRepository.update(channel);
		if (channel.enabled()) {
			this.joinChatRepository.joinChannel(channel);
			this.streamRepository.computeStatus(channel);
		}
		else {
			this.leaveChatRepository.leaveChannel(channel);
		}
	}
}
