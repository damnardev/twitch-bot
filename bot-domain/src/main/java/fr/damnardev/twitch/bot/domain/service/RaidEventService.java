package fr.damnardev.twitch.bot.domain.service;

import lombok.RequiredArgsConstructor;
import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.model.ChannelRaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.Message;
import fr.damnardev.twitch.bot.domain.model.event.RaidEvent;
import fr.damnardev.twitch.bot.domain.port.primary.IRaidEventService;
import fr.damnardev.twitch.bot.domain.port.primary.IRandomService;
import fr.damnardev.twitch.bot.domain.port.secondary.IChannelRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IMessageRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IShoutoutRepository;

@DomainService
@RequiredArgsConstructor
public class RaidEventService implements IRaidEventService {

    private final IChannelRepository channelRepository;

    private final IChannelRaidConfigurationRepository channelRaidConfigurationRepository;

    private final IShoutoutRepository shoutoutRepository;

    private final IMessageRepository messageRepository;

    private final IRandomService randomHandler;

    @Override
    public void process(RaidEvent raidEvent) {
        var channel = channelRepository.find(raidEvent.toUserName());
        if (channel == null || channel.isDisabled()) {
            return;
        }
        var channelRaid = channelRaidConfigurationRepository.find(channel);
        twitchShoutout(raidEvent, channelRaid, channel);
        wizebotShoutout(raidEvent, channelRaid, channel);
        sendingMessage(raidEvent, channelRaid, channel);
    }

    private void twitchShoutout(RaidEvent raidEvent, ChannelRaidConfiguration channelRaidConfiguration, ChannelInfo from) {
        if (channelRaidConfiguration.twitchShoutoutEnabled()) {
            var to = ChannelInfo.builder()
                                .id(Long.parseLong(raidEvent.fromUserId()))
                                .build();
            shoutoutRepository.send(from, to);
        }
    }

    private void wizebotShoutout(RaidEvent raidEvent, ChannelRaidConfiguration channelRaidConfiguration, ChannelInfo channelInfo) {
        if (channelRaidConfiguration.wizebotShoutoutEnabled()) {
            messageRepository.send(toMessage(channelInfo, "!so " + raidEvent.fromUserName()));
        }
    }

    private void sendingMessage(RaidEvent raidEvent, ChannelRaidConfiguration channelRaidConfiguration, ChannelInfo channelInfo) {
        if (!channelRaidConfiguration.raidMessageEnabled()) {
            return;
        }
        var message = randomHandler.getRandom(channelRaidConfiguration.messages());
        var formatted = String.format(message, raidEvent.fromUserName());
        messageRepository.send(toMessage(channelInfo, formatted));
    }

    private Message toMessage(ChannelInfo channel, String message) {
        return Message.builder()
                      .channel(channel)
                      .value(message)
                      .build();
    }

}