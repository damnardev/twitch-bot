package fr.damnardev.twitch.bot.core.service.command;

import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;
import fr.damnardev.twitch.bot.model.CommandType;
import fr.damnardev.twitch.bot.model.form.ChannelMessageEventForm;

public interface CommandInterpreter {

	void interpret(Channel channel, Command command, ChannelMessageEventForm form);

	CommandType getCommandTypeInterpreter();

}
