package fr.damnardev.twitch.bot.primary.adapter;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.events.ChannelRaidEvent;
import fr.damnardev.twitch.bot.domain.model.form.ChannelRaidEventForm;
import fr.damnardev.twitch.bot.domain.port.primary.ChannelRaidEventService;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ChannelRaidEventConsumer extends AbstractChannelEventConsumer<ChannelRaidEvent, ChannelRaidEventForm> {

	public ChannelRaidEventConsumer(TwitchClient twitchClient, ThreadPoolTaskExecutor executor, ChannelRaidEventService handler) {
		super(executor, twitchClient, handler, ChannelRaidEvent.class);
	}

	@Override
	protected ChannelRaidEventForm toModel(ChannelRaidEvent event) {
		var id = Long.parseLong(event.getToBroadcasterUserId());
		var name = event.getToBroadcasterUserLogin();
		var raiderId = Long.parseLong(event.getFromBroadcasterUserId());
		var raiderName = event.getFromBroadcasterUserLogin();
		return ChannelRaidEventForm.builder().channelId(id).channelName(name).raiderId(raiderId).raiderName(raiderName).build();
	}

}
