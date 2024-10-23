package fr.damnardev.twitch.bot.domain.service.event;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.StatusEvent;
import fr.damnardev.twitch.bot.domain.port.primary.event.IStatusEventService;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ISaveChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class StatusEventService implements IStatusEventService {

    private final IFindChannelRepository findChannelRepository;

    private final ISaveChannelRepository saveChannelRepository;

    @Override
    public void process(StatusEvent event) {
        var optional = findChannelRepository.find(event.channel());
        if (optional.isEmpty()) {
            return;
        }
        var channel = optional.get().toBuilder().online(event.online()).build();
        saveChannelRepository.save(channel);
    }

}
