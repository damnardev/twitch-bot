package fr.damnardev.twitch.bot.secondary.adapter;

import fr.damnardev.twitch.bot.database.repository.DbChannelCommandRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelCommand;
import fr.damnardev.twitch.bot.domain.model.ChannelCommandConfiguration;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindCommandConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FindCommandConfigurationRepository implements IFindCommandConfigurationRepository {

    private final DbChannelCommandRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ChannelCommandConfiguration find(ChannelCommand channelCommand) {
        return repository.find(channelCommand.channel().name(), channelCommand.name()).map(this::toModel).orElseGet(() -> ChannelCommandConfiguration.builder().build());
    }

    private ChannelCommandConfiguration toModel(fr.damnardev.twitch.bot.database.entity.ChannelCommand channelCommand) {
        return ChannelCommandConfiguration.builder().id(channelCommand.getId()).name(channelCommand.getName()).enabled(channelCommand.isEnabled()).lastExecution(channelCommand.getLastExecution()).cooldown(channelCommand.getCooldown()).build();
    }

}
