package fr.damnardev.twitch.bot.domain.service;

import lombok.RequiredArgsConstructor;
import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.primary.IAddChanelService;
import fr.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;

@DomainService
@RequiredArgsConstructor
public class AddChanelService implements IAddChanelService {

    private final IChannelRepository channelRepository;

    @Override
    public ChannelInfo process(ChannelInfo channel) {
        return channelRepository.addChannel(channel);
    }

}
