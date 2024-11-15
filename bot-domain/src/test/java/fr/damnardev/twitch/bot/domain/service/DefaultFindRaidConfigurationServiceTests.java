package fr.damnardev.twitch.bot.domain.service;

import java.util.ArrayList;
import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.event.ChannelRaidEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationFindEvent;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultFindRaidConfigurationServiceTests {

	private DefaultFindRaidConfigurationService findRaidConfigurationService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private FindRaidConfigurationRepository findRaidConfigurationRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.findRaidConfigurationService = new DefaultFindRaidConfigurationService(this.findChannelRepository, this.findRaidConfigurationRepository, this.eventPublisher, this.tryService);
	}

	@Test
	void findByChannelName_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var name = "name";
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findChannelRepository.findByName(name)).willThrow(exception);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.findRaidConfigurationService.findByChannelName(name);

		// Then
		then(this.tryService).should().doTry(any(), eq(name));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.eventPublisher);
	}

	@Test
	void findByChannelName_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var name = "name";
		var event = ChannelRaidEvent.builder().error("Channel not found").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.findRaidConfigurationService.findByChannelName(name);

		// Then
		then(this.tryService).should().doTry(any(), eq(name));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.eventPublisher);
	}

	@Test
	void findByChannelName_shouldPublishEvent_whenRaidConfigurationNotFound() {
		// Given
		var name = "name";
		var channel = Channel.builder().name(name).build();
		var event = ChannelRaidEvent.builder().error("Channel Raid Configuration not found").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannelName(name)).willReturn(Optional.empty());
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.findRaidConfigurationService.findByChannelName(name);

		// Then
		then(this.tryService).should().doTry(any(), eq(name));
		then(this.findChannelRepository).should().findByName(name);
		then(this.findRaidConfigurationRepository).should().findByChannelName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.eventPublisher);
	}

	@Test
	void delete_shouldPublishEvent_whenRaidConfigurationFound() {
		// Given
		var name = "name";
		var channel = Channel.builder().name(name).build();
		var messages = new ArrayList<String>();
		messages.add("message");
		messages.add("another message");
		var raidConfiguration = RaidConfiguration.builder().messages(messages).build();
		var event = RaidConfigurationFindEvent.builder().configuration(raidConfiguration).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannelName(name)).willReturn(Optional.of(raidConfiguration));
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.findRaidConfigurationService.findByChannelName(name);

		// Then
		then(this.tryService).should().doTry(any(), eq(name));
		then(this.findChannelRepository).should().findByName(name);
		then(this.findRaidConfigurationRepository).should().findByChannelName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.eventPublisher);
	}

}
