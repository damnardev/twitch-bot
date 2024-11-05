package fr.damnardev.twitch.bot.primary.adapter;

import com.github.philippheuer.events4j.core.EventManager;
import com.github.philippheuer.events4j.simple.SimpleEventHandler;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.common.events.domain.EventChannel;
import com.github.twitch4j.eventsub.events.ChannelRaidEvent;
import fr.damnardev.twitch.bot.domain.model.form.ChannelRaidEventForm;
import fr.damnardev.twitch.bot.domain.port.primary.ChannelRaidEventService;
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
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ChannelRaidEventConsumerTests {

	@InjectMocks
	private ChannelRaidEventConsumer consumer;

	@Mock
	private ThreadPoolTaskExecutor executor;

	@Mock
	private TwitchClient twitchClient;

	@Mock
	private ChannelRaidEventService handler;

	@Test
	void init_shouldRegisterHandler_whenCalled() {
		// Given
		var eventManager = mock(EventManager.class);
		var eventHandler = mock(SimpleEventHandler.class);

		given(this.twitchClient.getEventManager()).willReturn(eventManager);
		given(eventManager.getEventHandler(SimpleEventHandler.class)).willReturn(eventHandler);
		given(eventHandler.onEvent(eq(ChannelRaidEvent.class), any())).willReturn(null);

		// When
		this.consumer.init();

		// Then
		then(this.twitchClient).should().getEventManager();
		then(eventManager).should().getEventHandler(SimpleEventHandler.class);
		then(eventHandler).should().onEvent(eq(ChannelRaidEvent.class), any());
		verifyNoMoreInteractions(this.executor, this.twitchClient, this.handler, eventManager, eventHandler);
	}

	@Test
	void process_shouldExecuteHandler_whenCalled() {
		// Given
		var captor = ArgumentCaptor.forClass(Runnable.class);
		var event = mock(ChannelRaidEvent.class);
		var channel = mock(EventChannel.class);
		var model = ChannelRaidEventForm.builder().id(2L).name("channel").raiderId(1L).raiderName("raider").build();

		doNothing().when(this.executor).execute(captor.capture());
		given(event.getFromBroadcasterUserLogin()).willReturn("raider");
		given(event.getFromBroadcasterUserId()).willReturn("1");
		given(event.getToBroadcasterUserLogin()).willReturn("channel");
		given(event.getToBroadcasterUserId()).willReturn("2");
		doNothing().when(this.handler).process(model);

		// When
		this.consumer.process(event);

		// Then
		then(this.executor).should().execute(captor.capture());
		captor.getValue().run();
		then(event).should().getFromBroadcasterUserLogin();
		then(event).should().getFromBroadcasterUserId();
		then(event).should().getToBroadcasterUserId();
		then(event).should().getToBroadcasterUserLogin();
		then(this.handler).should().process(model);
		verifyNoMoreInteractions(this.executor, this.twitchClient, this.handler, event, channel);
	}

}
