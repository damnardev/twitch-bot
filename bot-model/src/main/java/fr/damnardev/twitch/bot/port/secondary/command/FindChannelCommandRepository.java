package fr.damnardev.twitch.bot.port.secondary.command;

import java.util.Map;

import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;

public interface FindChannelCommandRepository {

	Map<String, Command> findByChannel(Channel channel);

}
