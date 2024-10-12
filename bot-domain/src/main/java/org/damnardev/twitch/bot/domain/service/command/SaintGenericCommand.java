package org.damnardev.twitch.bot.domain.service.command;

import lombok.RequiredArgsConstructor;
import org.damnardev.twitch.bot.domain.DomainService;
import org.damnardev.twitch.bot.domain.model.event.MessageEvent;
import org.damnardev.twitch.bot.domain.port.secondary.ISaintRepository;

@DomainService
@RequiredArgsConstructor
public class SaintGenericCommand implements IGenericCommand {

    private final ISaintRepository saintRepository;

    @Override
    public String name() {
        return "!saints";
    }

    @Override
    public String process(MessageEvent event) {
        return saintRepository.find()
                              .value();
    }

}
