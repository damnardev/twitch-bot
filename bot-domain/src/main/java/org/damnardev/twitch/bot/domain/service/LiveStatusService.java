package org.damnardev.twitch.bot.domain.service;

import lombok.RequiredArgsConstructor;
import org.damnardev.twitch.bot.domain.DomainService;
import org.damnardev.twitch.bot.domain.model.event.LiveStatusEvent;
import org.damnardev.twitch.bot.domain.port.primary.ILiveStatusService;
import org.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;

@DomainService
@RequiredArgsConstructor
public class LiveStatusService implements ILiveStatusService {

    private final IChannelRepository repository;

    @Override
    public void process(LiveStatusEvent event) {
        var channel = repository.find(event.broadcasterIdUserName());
        if (channel == null) {
            return;
        }
        if (event.online()) {
            repository.setOnline(channel);
        } else {
            repository.setOffline(channel);
        }
    }

}
