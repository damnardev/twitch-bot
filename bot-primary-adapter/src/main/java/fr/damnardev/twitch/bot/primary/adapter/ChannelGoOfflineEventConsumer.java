package fr.damnardev.twitch.bot.primary.adapter;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import fr.damnardev.twitch.bot.model.form.UpdateChannelForm;
import fr.damnardev.twitch.bot.port.primary.channel.UpdateChannelService;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ChannelGoOfflineEventConsumer extends AbstractChannelEventConsumer<ChannelGoOfflineEvent, UpdateChannelForm> {

	public ChannelGoOfflineEventConsumer(TwitchClient twitchClient, ThreadPoolTaskExecutor executor, UpdateChannelService handler) {
		super(executor, twitchClient, handler, ChannelGoOfflineEvent.class);
	}

	@Override
	protected UpdateChannelForm toModel(ChannelGoOfflineEvent event) {
		var channel = event.getChannel();
		var id = Long.parseLong(channel.getId());
		var name = channel.getName();
		return UpdateChannelForm.builder().id(id).name(name).online(false).build();
	}

}
