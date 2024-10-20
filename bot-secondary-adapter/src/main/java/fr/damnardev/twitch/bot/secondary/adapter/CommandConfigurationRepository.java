package fr.damnardev.twitch.bot.secondary.adapter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import fr.damnardev.twitch.bot.database.entity.ChannelCommand;
import fr.damnardev.twitch.bot.database.repository.DbChannelCommandRepository;
import fr.damnardev.twitch.bot.domain.model.Command;
import fr.damnardev.twitch.bot.domain.model.CommandConfiguration;
import fr.damnardev.twitch.bot.domain.port.secondary.ICommandConfigurationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@RequiredArgsConstructor
@Service
@Slf4j
public class CommandConfigurationRepository implements ICommandConfigurationRepository {

    private final DbChannelCommandRepository repository;

    @Override
    @Transactional(readOnly = true)
    public CommandConfiguration find(Command command) {
        return repository.find(command.channel()
                                      .name(), command.name())
                         .map(this::toModel)
                         .orElseGet(() -> CommandConfiguration.builder()
                                                              .build());
    }

    private CommandConfiguration toModel(ChannelCommand command) {
        return CommandConfiguration.builder()
                                   .id(command.getId())
                                   .name(command.getName())
                                   .enabled(command.isEnabled())
                                   .lastExecution(command.getLastExecution())
                                   .cooldown(command.getCooldown())
                                   .build();
    }

    @Override
    @Transactional
    public void updateLastExecution(CommandConfiguration commandConfiguration) {
        var entity = repository.findById(commandConfiguration.id());
        entity.ifPresent(command -> {
            command.setLastExecution(OffsetDateTime.now());
            repository.saveAndFlush(command);
        });
    }

}
