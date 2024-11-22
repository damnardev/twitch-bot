package fr.damnardev.twitch.bot.domain.port.secondary.command;

import java.util.Map;

import fr.damnardev.twitch.bot.domain.model.Command;

public interface FindChannelCommandRepository {

	Map<String, Command> findByChannelName(String name);

}