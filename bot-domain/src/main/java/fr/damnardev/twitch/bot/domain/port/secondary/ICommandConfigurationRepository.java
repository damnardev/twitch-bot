package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.Command;
import fr.damnardev.twitch.bot.domain.model.CommandConfiguration;

public interface ICommandConfigurationRepository {

    CommandConfiguration find(Command command);

    void updateLastExecution(CommandConfiguration commandConfiguration);

}
