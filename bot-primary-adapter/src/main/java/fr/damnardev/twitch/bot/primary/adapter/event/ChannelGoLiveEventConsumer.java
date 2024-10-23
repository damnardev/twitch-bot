package fr.damnardev.twitch.bot.primary.adapter.event;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.model.event.StatusEvent;
import fr.damnardev.twitch.bot.domain.port.primary.event.IStatusEventService;
import fr.damnardev.twitch.bot.primary.adapter.AbstractChannelEventConsumer;

import org.springframework.stereotype.Service;

@Service
public class ChannelGoLiveEventConsumer extends AbstractChannelEventConsumer<ChannelGoLiveEvent, StatusEvent> {

	public ChannelGoLiveEventConsumer(TwitchClient twitchClient, IStatusEventService handler) {
		super(twitchClient, handler, ChannelGoLiveEvent.class);
	}

	@Override
	protected StatusEvent toModel(ChannelGoLiveEvent event) {
		var eventChannel = event.getChannel();
		var channel = User.builder().name(eventChannel.getName()).id(Long.parseLong(eventChannel.getId())).build();
		return StatusEvent.builder().channel(channel).online(true).build();
	}

}
