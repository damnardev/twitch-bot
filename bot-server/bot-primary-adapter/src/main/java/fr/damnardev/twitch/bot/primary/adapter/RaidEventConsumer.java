package fr.damnardev.twitch.bot.primary.adapter;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.chat.events.channel.RaidEvent;
import fr.damnardev.twitch.bot.model.form.ChannelRaidEventForm;
import fr.damnardev.twitch.bot.port.primary.ChannelRaidEventService;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class RaidEventConsumer extends AbstractChannelEventConsumer<RaidEvent, ChannelRaidEventForm> {

	public RaidEventConsumer(TwitchClient twitchClient, ThreadPoolTaskExecutor executor, ChannelRaidEventService handler) {
		super(executor, twitchClient, handler, RaidEvent.class);
	}

	@Override
	protected ChannelRaidEventForm toModel(RaidEvent event) {
		var id = Long.parseLong(event.getChannel().getId());
		var name = event.getChannel().getName();
		var raiderId = Long.parseLong(event.getRaider().getId());
		var raiderName = event.getRaider().getName();
		return ChannelRaidEventForm.builder().channelId(id).channelName(name).raiderId(raiderId).raiderName(raiderName).build();
	}

}
