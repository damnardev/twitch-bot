package fr.damnardev.twitch.bot.domain.service.command;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.Command;
import fr.damnardev.twitch.bot.domain.model.form.ChannelMessageEventForm;

public interface CommandInterpreter {

	void interpret(Channel channel, Command command, ChannelMessageEventForm form);

	boolean canInterpret(Channel channel, Command command, ChannelMessageEventForm form);

}
