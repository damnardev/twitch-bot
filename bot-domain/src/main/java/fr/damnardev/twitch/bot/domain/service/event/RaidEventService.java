package fr.damnardev.twitch.bot.domain.service.event;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.ChannelRaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.Message;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.model.event.RaidEvent;
import fr.damnardev.twitch.bot.domain.port.primary.IRandomService;
import fr.damnardev.twitch.bot.domain.port.primary.event.IRaidEventService;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IMessageRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IShoutoutRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class RaidEventService implements IRaidEventService {

    public static final String SO_STRING = "!so %s";

    private final IFindChannelRepository findChannelRepository;

    private final IFindChannelRaidConfigurationRepository channelRaidConfigurationRepository;

    private final IShoutoutRepository shoutoutRepository;

    private final IMessageRepository messageRepository;

    private final IRandomService randomService;

    @Override
    public void process(RaidEvent raidEvent) {
        var channel = findChannelRepository.find(raidEvent.channel()).orElse(null);
        if (channel == null || channel.isDisabled()) {
            return;
        }
        var user = channel.user();
        var configuration = channelRaidConfigurationRepository.find(user);
        var raider = raidEvent.raider();
        twitchShoutout(user, raider, configuration);
        wizebotShoutout(user, raider, configuration);
        sendingMessage(user, raider, configuration);
    }

    private void twitchShoutout(User channel, User raider, ChannelRaidConfiguration configuration) {
        if (configuration.twitchShoutoutEnabled()) {
            shoutoutRepository.send(channel, raider);
        }
    }

    private void wizebotShoutout(User channel, User raider, ChannelRaidConfiguration configuration) {
        if (configuration.wizebotShoutoutEnabled()) {
            var message = toMessage(channel, SO_STRING.formatted(raider.name()));
            messageRepository.send(message);
        }
    }

    private void sendingMessage(User channel, User raider, ChannelRaidConfiguration configuration) {
        if (!configuration.raidMessageEnabled()) {
            return;
        }
        var message = randomService.getRandom(configuration.messages());
        var formatted = String.format(message, raider.name());
        messageRepository.send(toMessage(channel, formatted));
    }

    private Message toMessage(User channel, String message) {
        return Message.builder().channel(channel).value(message).build();
    }

}
