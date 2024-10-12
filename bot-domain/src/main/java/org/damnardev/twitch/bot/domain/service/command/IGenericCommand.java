package org.damnardev.twitch.bot.domain.service.command;

import org.damnardev.twitch.bot.domain.model.event.MessageEvent;

public interface IGenericCommand {

    String name();

    String process(MessageEvent event);

}
