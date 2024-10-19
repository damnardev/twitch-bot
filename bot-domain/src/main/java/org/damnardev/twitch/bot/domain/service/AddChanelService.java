package org.damnardev.twitch.bot.domain.service;

import lombok.RequiredArgsConstructor;
import org.damnardev.twitch.bot.domain.DomainService;
import org.damnardev.twitch.bot.domain.model.ChannelInfo;
import org.damnardev.twitch.bot.domain.port.primary.IAddChanelService;
import org.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;

@DomainService
@RequiredArgsConstructor
public class AddChanelService implements IAddChanelService {

    private final IChannelRepository channelRepository;

    @Override
    public ChannelInfo process(ChannelInfo channel) {
        return channelRepository.addChannel(channel);
    }

}
