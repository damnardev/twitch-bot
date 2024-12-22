package fr.damnardev.twitch.bot.primary.adapter;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.events.ChannelGoOfflineEvent;
import fr.damnardev.twitch.bot.model.form.UpdateChannelForm;
import fr.damnardev.twitch.bot.port.primary.channel.UpdateChannelService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ChannelGoOfflineEventConsumerTests {

	@InjectMocks
	private ChannelGoOfflineEventConsumer consumer;

	@Mock
	private ThreadPoolTaskExecutor executor;

	@Mock
	private TwitchClient twitchClient;

	@Mock
	private UpdateChannelService handler;

	@Test
	void init_shouldRegisterHandler_whenCalled() {
		// Given
		var eventManager = mock(EventManager.class);
		var eventHandler = mock(SimpleEventHandler.class);

		given(this.twitchClient.getEventManager()).willReturn(eventManager);
		given(eventManager.getEventHandler(SimpleEventHandler.class)).willReturn(eventHandler);
		given(eventHandler.onEvent(eq(ChannelGoOfflineEvent.class), any())).willReturn(null);

		// When
		this.consumer.init();

		// Then
		then(this.twitchClient).should().getEventManager();
		then(eventManager).should().getEventHandler(SimpleEventHandler.class);
		then(eventHandler).should().onEvent(eq(ChannelGoOfflineEvent.class), any());
		verifyNoMoreInteractions(this.executor, this.twitchClient, this.handler, eventManager, eventHandler);
	}

	@Test
	void process_shouldExecuteHandler_whenCalled() {
		// Given
		var captor = ArgumentCaptor.forClass(Runnable.class);
		var event = mock(ChannelGoOfflineEvent.class);
		var channel = mock(EventChannel.class);
		var model = UpdateChannelForm.builder().id(1L).name("channelName").online(false).build();

		given(event.getChannel()).willReturn(channel);
		given(channel.getId()).willReturn("1");
		given(channel.getName()).willReturn("channelName");

		// When
		this.consumer.process(event);

		// Then
		then(this.executor).should().execute(captor.capture());
		captor.getValue().run();
		then(event).should().getChannel();
		then(channel).should().getId();
		then(channel).should().getName();
		then(this.handler).should().process(model);
		verifyNoMoreInteractions(this.executor, this.twitchClient, this.handler, event, channel);
	}

}
