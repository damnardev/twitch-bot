package fr.damnardev.twitch.bot.primary.adapter;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.ChannelMessageEvent;
import fr.damnardev.twitch.bot.domain.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.domain.port.primary.ChannelMessageEventService;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ChannelMessageEventConsumer extends AbstractChannelEventConsumer<ChannelMessageEvent, ChannelMessageEventForm> {

	public ChannelMessageEventConsumer(TwitchClient twitchClient, ThreadPoolTaskExecutor executor, ChannelMessageEventService handler) {
		super(executor, twitchClient, handler, ChannelMessageEvent.class);
	}

	@Override
	protected ChannelMessageEventForm toModel(ChannelMessageEvent event) {
		var eventUser = event.getUser();
		var eventChannel = event.getChannel();
		return ChannelMessageEventForm.builder().id(Long.parseLong(eventChannel.getId())).name(eventChannel.getName()).sender(eventUser.getName()).message(event.getMessage()).build();
	}

}