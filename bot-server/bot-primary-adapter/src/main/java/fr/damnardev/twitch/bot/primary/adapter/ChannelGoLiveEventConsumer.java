package fr.damnardev.twitch.bot.primary.adapter;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import fr.damnardev.twitch.bot.model.form.UpdateChannelForm;
import fr.damnardev.twitch.bot.port.primary.channel.UpdateChannelService;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ChannelGoLiveEventConsumer extends AbstractChannelEventConsumer<ChannelGoLiveEvent, UpdateChannelForm> {

	public ChannelGoLiveEventConsumer(TwitchClient twitchClient, ThreadPoolTaskExecutor executor, UpdateChannelService handler) {
		super(executor, twitchClient, handler, ChannelGoLiveEvent.class);
	}

	@Override
	protected UpdateChannelForm toModel(ChannelGoLiveEvent event) {
		var channel = event.getChannel();
		var id = Long.parseLong(channel.getId());
		var name = channel.getName();
		return UpdateChannelForm.builder().id(id).name(name).online(true).build();
	}

}
