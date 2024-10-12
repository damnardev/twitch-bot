package org.damnardev.twitch.bot.domain.port.secondary;

import org.damnardev.twitch.bot.domain.model.Command;
import org.damnardev.twitch.bot.domain.model.CommandConfiguration;

public interface ICommandConfigurationRepository {

    CommandConfiguration find(Command command);

    void updateLastExecution(CommandConfiguration commandConfiguration);

}
