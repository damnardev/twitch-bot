package fr.damnardev.twitch.bot.domain.service.command;

import fr.damnardev.twitch.bot.domain.model.event.MessageEvent;

public interface IGenericCommand {

    String name();

    String process(MessageEvent event);

}
