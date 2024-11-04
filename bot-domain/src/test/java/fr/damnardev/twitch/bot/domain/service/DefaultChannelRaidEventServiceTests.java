package fr.damnardev.twitch.bot.domain.service;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.Message;
import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.Shoutout;
import fr.damnardev.twitch.bot.domain.model.event.ChannelRaidEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.form.ChannelRaidEventForm;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.MessageRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ShoutoutRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.spy;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultChannelRaidEventServiceTests {

	private DefaultChannelRaidEventService channelRaidEventService;

	private DefaultTryService tryService;

	private DefaultRandomService randomService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private FindRaidConfigurationRepository findRaidConfigurationRepository;

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private ShoutoutRepository shoutoutRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.randomService = new DefaultRandomService(new Random(0));
		this.tryService = spy(this.tryService);
		this.randomService = spy(this.randomService);
		this.channelRaidEventService = new DefaultChannelRaidEventService(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.messageRepository, this.shoutoutRepository, this.randomService, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var name = "name";
		var form = ChannelRaidEventForm.builder().name(name).build();
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findChannelRepository.findByName(name)).willThrow(exception);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.channelRaidEventService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.messageRepository, this.shoutoutRepository, this.randomService, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var name = "name";
		var form = ChannelRaidEventForm.builder().name(name).build();
		var event = ChannelRaidEvent.builder().error("Channel not found").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.channelRaidEventService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.messageRepository, this.shoutoutRepository, this.randomService, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenRaidConfigurationNotFound() {
		// Given
		var name = "name";
		var form = ChannelRaidEventForm.builder().name(name).build();
		var channel = Channel.builder().name(name).build();
		var event = ChannelRaidEvent.builder().error("Channel Raid Configuration not found").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannelName(name)).willReturn(Optional.empty());
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.channelRaidEventService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.findRaidConfigurationRepository).should().findByChannelName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.messageRepository, this.shoutoutRepository, this.randomService, this.eventPublisher);
	}

	@ParameterizedTest
	@CsvSource({ "true,true,true", "true,true,false", "true,false,true", "true,false,false", "false,true,true", "false,true,false", "false,false,true", "false,false,false" })
	void process_shouldSendMessageAndShoutout_whenNeeded(boolean raidMessageEnabled, boolean wizebotShoutoutEnabled, boolean twitchShoutoutEnabled) {
		// Given
		var name = "name";
		var form = ChannelRaidEventForm.builder().id(1L).name(name).raiderId(2L).raiderName("raiderName").build();
		var channel = Channel.builder().name(name).build();
		var messages = Collections.singletonList("hi %s");
		var configuration = RaidConfiguration.builder().raidMessageEnabled(raidMessageEnabled).wizebotShoutoutEnabled(wizebotShoutoutEnabled).twitchShoutoutEnabled(twitchShoutoutEnabled).messages(messages).build();

		var expectedMessageHi = Message.builder().channelId(channel.id()).channelName(name).content("hi raiderName").build();
		var expectedMessageSo = Message.builder().channelId(channel.id()).channelName(name).content("!so raiderName").build();
		var expectedShoutout = Shoutout.builder().raiderId(form.raiderId()).raiderName(form.raiderName()).channelId(channel.id()).channelName(name).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannelName(name)).willReturn(Optional.of(configuration));
		if (raidMessageEnabled) {
			doNothing().when(this.messageRepository).sendMessage(expectedMessageHi);
		}
		if (wizebotShoutoutEnabled) {
			doNothing().when(this.messageRepository).sendMessage(expectedMessageSo);
		}
		if (twitchShoutoutEnabled) {
			doNothing().when(this.shoutoutRepository).sendShoutout(expectedShoutout);
		}

		// When
		this.channelRaidEventService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.findRaidConfigurationRepository).should().findByChannelName(name);
		if (raidMessageEnabled) {
			then(this.randomService).should().getRandom(messages);
			then(this.messageRepository).should().sendMessage(expectedMessageHi);
		}
		if (wizebotShoutoutEnabled) {
			then(this.messageRepository).should().sendMessage(expectedMessageSo);
		}
		if (twitchShoutoutEnabled) {
			then(this.shoutoutRepository).should().sendShoutout(expectedShoutout);
		}
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.messageRepository, this.shoutoutRepository, this.randomService, this.eventPublisher);
	}

}
