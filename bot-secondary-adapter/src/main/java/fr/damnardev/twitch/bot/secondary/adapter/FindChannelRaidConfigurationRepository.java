package fr.damnardev.twitch.bot.secondary.adapter;

import fr.damnardev.twitch.bot.database.entity.ChannelRaid;
import fr.damnardev.twitch.bot.database.repository.DbChannelRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelRaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRaidConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FindChannelRaidConfigurationRepository implements IFindChannelRaidConfigurationRepository {

    private final DbChannelRaidConfigurationRepository dbChannelRaidConfigurationRepository;

    private ChannelRaidConfiguration toModel(ChannelRaid configuration) {
        return ChannelRaidConfiguration.builder().twitchShoutoutEnabled(configuration.isTwitchShoutoutEnabled()).wizebotShoutoutEnabled(configuration.isWizebotShoutoutEnabled()).raidMessageEnabled(configuration.isRaidMessageEnabled()).messages(configuration.getMessages().stream().toList()).build();
    }

    @Override
    @Transactional(readOnly = true)
    public ChannelRaidConfiguration find(User channel) {
        return dbChannelRaidConfigurationRepository.find(channel.id()).map(this::toModel).orElseGet(() -> ChannelRaidConfiguration.builder().build());
    }

}
