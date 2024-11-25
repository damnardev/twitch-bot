package fr.damnardev.twitch.bot.core.service;

import java.util.Collections;
import java.util.Optional;
import java.util.Random;

import fr.damnardev.twitch.bot.exception.BusinessException;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Message;
import fr.damnardev.twitch.bot.model.RaidConfiguration;
import fr.damnardev.twitch.bot.model.Shoutout;
import fr.damnardev.twitch.bot.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.model.form.ChannelRaidEventForm;
import fr.damnardev.twitch.bot.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.port.secondary.MessageRepository;
import fr.damnardev.twitch.bot.port.secondary.ShoutoutRepository;
import fr.damnardev.twitch.bot.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.port.secondary.raid.FindRaidConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.spy;
import static org.mockito.BDDMockito.then;
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
		this.channelRaidEventService = new DefaultChannelRaidEventService(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.messageRepository, this.shoutoutRepository, this.randomService);
	}

	@Test
	void process_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var channelName = "channelName";
		var form = ChannelRaidEventForm.builder().channelName(channelName).build();
		var captor = ArgumentCaptor.forClass(ErrorEvent.class);

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.empty());

		// When
		this.channelRaidEventService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.eventPublisher).should().publish(captor.capture());
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.messageRepository, this.shoutoutRepository, this.randomService, this.eventPublisher);

		var event = captor.getValue();
		assertThat(event.getException()).isNotNull()
				.isInstanceOf(BusinessException.class)
				.hasMessage("Channel not found");
	}

	@ParameterizedTest
	@CsvSource({ "false,false", "false,true", "true,false" })
	void process_shouldPublishEvent_whenChannelOffline(boolean enabled, boolean online) {
		// Given
		var channelName = "channelName";
		var form = ChannelRaidEventForm.builder().channelName(channelName).build();
		var captor = ArgumentCaptor.forClass(ErrorEvent.class);

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(Channel.builder().name(channelName).enabled(enabled).online(online).build()));

		// When
		this.channelRaidEventService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.messageRepository, this.shoutoutRepository, this.randomService, this.eventPublisher);
	}

	@Test
	void process_shouldPublishEvent_whenRaidConfigurationNotFound() {
		// Given
		var channelName = "channelName";
		var form = ChannelRaidEventForm.builder().channelName(channelName).build();
		var channel = Channel.builder().name(channelName).enabled(true).online(true).build();
		var captor = ArgumentCaptor.forClass(ErrorEvent.class);

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannel(channel)).willReturn(Optional.empty());

		// When
		this.channelRaidEventService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findRaidConfigurationRepository).should().findByChannel(channel);
		then(this.eventPublisher).should().publish(captor.capture());
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.messageRepository, this.shoutoutRepository, this.randomService, this.eventPublisher);

		var event = captor.getValue();
		assertThat(event.getException()).isNotNull()
				.isInstanceOf(BusinessException.class)
				.hasMessage("Channel Raid Configuration not found");
	}

	@ParameterizedTest
	@CsvSource({ "true,true,true", "true,true,false", "true,false,true", "true,false,false", "false,true,true", "false,true,false", "false,false,true", "false,false,false" })
	void process_shouldSendMessageAndShoutout_whenNeeded(boolean raidMessageEnabled, boolean wizebotShoutoutEnabled, boolean twitchShoutoutEnabled) {
		// Given
		var channelName = "channelName";
		var form = ChannelRaidEventForm.builder().channelId(1L).channelName(channelName).raiderId(2L).raiderName("raiderName").build();
		var channel = Channel.builder().name(channelName).enabled(true).online(true).build();
		var messages = Collections.singletonList("hi %s");
		var configuration = RaidConfiguration.builder().raidMessageEnabled(raidMessageEnabled).wizebotShoutoutEnabled(wizebotShoutoutEnabled).twitchShoutoutEnabled(twitchShoutoutEnabled).messages(messages).build();

		var expectedMessageHi = Message.builder().channelId(channel.id()).channelName(channelName).content("hi raiderName").build();
		var expectedMessageSo = Message.builder().channelId(channel.id()).channelName(channelName).content("!so raiderName").build();
		var expectedShoutout = Shoutout.builder().raiderId(form.raiderId()).raiderName(form.raiderName()).channelId(channel.id()).channelName(channelName).build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannel(channel)).willReturn(Optional.of(configuration));

		// When
		this.channelRaidEventService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findRaidConfigurationRepository).should().findByChannel(channel);
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
