package fr.damnardev.twitch.bot.domain.service.raid;

import java.util.Optional;
import java.util.stream.Stream;

import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.UpdateRaidConfigurationForm;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.raid.FindRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.raid.UpdateRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.service.DefaultTryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultUpdateRaidConfigurationMessageServiceTests {

	private DefaultUpdateRaidConfigurationMessageService updateRaidConfigurationMessageService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private FindRaidConfigurationRepository findRaidConfigurationRepository;

	@Mock
	private UpdateRaidConfigurationRepository updateRaidConfigurationRepository;

	@Mock
	private EventPublisher eventPublisher;

	private static Stream<Arguments> provideBoolean() {
		return Stream.of(Arguments.of(Boolean.TRUE, Boolean.FALSE, Boolean.FALSE), Arguments.of(Boolean.FALSE, Boolean.TRUE, Boolean.TRUE), Arguments.of(Boolean.TRUE, null, Boolean.TRUE));
	}

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.updateRaidConfigurationMessageService = new DefaultUpdateRaidConfigurationMessageService(this.findChannelRepository, this.findRaidConfigurationRepository, this.eventPublisher, this.tryService, this.updateRaidConfigurationRepository);
	}

	@Test
	void process_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var channelName = "channelName";
		var form = UpdateRaidConfigurationForm.builder().channelName(channelName).build();
		var captor = ArgumentCaptor.forClass(ErrorEvent.class);

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.empty());

		// When
		this.updateRaidConfigurationMessageService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.eventPublisher).should().publish(captor.capture());
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.updateRaidConfigurationRepository, this.eventPublisher);

		var event = captor.getValue();
		assertThat(event.getException()).isNotNull().isInstanceOf(BusinessException.class).hasMessage("Channel not found");
	}

	@Test
	void process_shouldPublishEvent_whenRaidConfigurationNotFound() {
		// Given
		var channelName = "channelName";
		var form = UpdateRaidConfigurationForm.builder().channelName(channelName).build();
		var channel = Channel.builder().name(channelName).build();
		var captor = ArgumentCaptor.forClass(ErrorEvent.class);

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannel(channel)).willReturn(Optional.empty());

		// When
		this.updateRaidConfigurationMessageService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findRaidConfigurationRepository).should().findByChannel(channel);
		then(this.eventPublisher).should().publish(captor.capture());
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.updateRaidConfigurationRepository, this.eventPublisher);

		var event = captor.getValue();
		assertThat(event.getException()).isNotNull().isInstanceOf(BusinessException.class).hasMessage("Channel Raid Configuration not found");
	}

	@ParameterizedTest
	@MethodSource("provideBoolean")
	void process_shouldUpdateAndPublishEvent_whenRaidConfigurationFoundAndWizebotShoutoutUpdated(Boolean value, Boolean newValue, Boolean expectedValue) {
		// Given
		var channelName = "channelName";
		var form = UpdateRaidConfigurationForm.builder().channelName(channelName).wizebotShoutoutEnabled(newValue).build();
		var channel = Channel.builder().name(channelName).build();

		var raidConfiguration = RaidConfiguration.builder().wizebotShoutoutEnabled(value).build();
		var updatedRaidConfiguration = RaidConfiguration.builder().wizebotShoutoutEnabled(expectedValue).build();
		var event = RaidConfigurationUpdatedEvent.builder().raidConfiguration(updatedRaidConfiguration).build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannel(channel)).willReturn(Optional.of(raidConfiguration));

		// When
		this.updateRaidConfigurationMessageService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findRaidConfigurationRepository).should().findByChannel(channel);
		then(this.updateRaidConfigurationRepository).should().update(updatedRaidConfiguration);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.updateRaidConfigurationRepository, this.eventPublisher);
	}

	@ParameterizedTest
	@MethodSource("provideBoolean")
	void process_shouldUpdateAndPublishEvent_whenRaidConfigurationFoundAndTwitchShoutoutUpdated(Boolean value, Boolean newValue, Boolean expectedValue) {
		// Given
		var channelName = "channelName";
		var form = UpdateRaidConfigurationForm.builder().channelName(channelName).twitchShoutoutEnabled(newValue).build();
		var channel = Channel.builder().name(channelName).build();

		var raidConfiguration = RaidConfiguration.builder().twitchShoutoutEnabled(value).build();
		var updatedRaidConfiguration = RaidConfiguration.builder().twitchShoutoutEnabled(expectedValue).build();
		var event = RaidConfigurationUpdatedEvent.builder().raidConfiguration(updatedRaidConfiguration).build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannel(channel)).willReturn(Optional.of(raidConfiguration));

		// When
		this.updateRaidConfigurationMessageService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findRaidConfigurationRepository).should().findByChannel(channel);
		then(this.updateRaidConfigurationRepository).should().update(updatedRaidConfiguration);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.updateRaidConfigurationRepository, this.eventPublisher);
	}

	@ParameterizedTest
	@MethodSource("provideBoolean")
	void process_shouldUpdateAndPublishEvent_whenRaidConfigurationFoundAndRaidMessageUpdated(Boolean value, Boolean newValue, Boolean expectedValue) {
		// Given
		var channelName = "channelName";
		var form = UpdateRaidConfigurationForm.builder().channelName(channelName).raidMessageEnabled(newValue).build();
		var channel = Channel.builder().name(channelName).build();

		var raidConfiguration = RaidConfiguration.builder().raidMessageEnabled(value).build();
		var updatedRaidConfiguration = RaidConfiguration.builder().raidMessageEnabled(expectedValue).build();
		var event = RaidConfigurationUpdatedEvent.builder().raidConfiguration(updatedRaidConfiguration).build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannel(channel)).willReturn(Optional.of(raidConfiguration));

		// When
		this.updateRaidConfigurationMessageService.process(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findRaidConfigurationRepository).should().findByChannel(channel);
		then(this.updateRaidConfigurationRepository).should().update(updatedRaidConfiguration);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.updateRaidConfigurationRepository, this.eventPublisher);
	}

}
