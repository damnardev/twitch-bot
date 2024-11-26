package fr.damnardev.twitch.bot.port.secondary.command;

import java.util.Optional;

import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;

public interface FindChannelCommandRepository {

	Optional<Command> findByChannelAndName(Channel channel, String name);

}
