package fr.damnardev.twitch.bot.primary.adapter;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.events.ChannelGoLiveEvent;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelOnlineForm;
import fr.damnardev.twitch.bot.domain.port.primary.UpdateChannelOnlineService;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;

@Service
public class ChannelGoLiveEventConsumer extends AbstractChannelEventConsumer<ChannelGoLiveEvent, UpdateChannelOnlineForm> {

	public ChannelGoLiveEventConsumer(TwitchClient twitchClient, ThreadPoolTaskExecutor executor, UpdateChannelOnlineService handler) {
		super(executor, twitchClient, handler, ChannelGoLiveEvent.class);
	}

	@Override
	protected UpdateChannelOnlineForm toModel(ChannelGoLiveEvent event) {
		var channel = event.getChannel();
		var id = Long.parseLong(channel.getId());
		var name = channel.getName();
		return UpdateChannelOnlineForm.builder().id(id).name(name).online(true).build();
	}

}
