package fr.damnardev.twitch.bot.secondary.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.damnardev.twitch.bot.database.entity.ChannelRaid;
import fr.damnardev.twitch.bot.database.repository.DbChannelRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.model.ChannelRaidConfiguration;
import fr.damnardev.twitch.bot.domain.port.secondary.IChannelRaidConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChannelRaidConfigurationRepository implements IChannelRaidConfigurationRepository {

    private final DbChannelRaidConfigurationRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ChannelRaidConfiguration find(ChannelInfo channel) {
        return repository.find(channel.id())
                         .map(this::toModel)
                         .orElseGet(() -> ChannelRaidConfiguration.builder()
                                                                  .build());
    }

    private ChannelRaidConfiguration toModel(ChannelRaid configuration) {
        return ChannelRaidConfiguration.builder()
                                       .twitchShoutoutEnabled(configuration.isTwitchShoutoutEnabled())
                                       .wizebotShoutoutEnabled(configuration.isWizebotShoutoutEnabled())
                                       .raidMessageEnabled(configuration.isRaidMessageEnabled())
                                       .messages(configuration.getMessages()
                                                              .stream()
                                                              .toList())
                                       .build();
    }

}
