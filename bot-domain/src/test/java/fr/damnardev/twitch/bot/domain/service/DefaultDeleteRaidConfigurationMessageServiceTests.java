package fr.damnardev.twitch.bot.domain.service;

import java.util.ArrayList;
import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.event.ChannelRaidEvent;
import fr.damnardev.twitch.bot.domain.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.DeleteRaidConfigurationMessageForm;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateRaidConfigurationRepository;
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
class DefaultDeleteRaidConfigurationMessageServiceTests {

	private DefaultDeleteRaidConfigurationMessageService deleteRaidConfigurationMessageService;

	private DefaultTryService tryService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private FindRaidConfigurationRepository findRaidConfigurationRepository;

	@Mock
	private UpdateRaidConfigurationRepository updateRaidConfigurationRepository;

	@Mock
	private EventPublisher eventPublisher;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		this.deleteRaidConfigurationMessageService = new DefaultDeleteRaidConfigurationMessageService(this.findChannelRepository, this.findRaidConfigurationRepository, this.eventPublisher, this.tryService, this.updateRaidConfigurationRepository);
	}


	@Test
	void delete_shouldPublishEvent_whenExceptionThrown() {
		// Given
		var name = "name";
		var form = DeleteRaidConfigurationMessageForm.builder().name(name).build();
		var exception = new RuntimeException();
		var event = ErrorEvent.builder().exception(exception).build();

		given(this.findChannelRepository.findByName(name)).willThrow(exception);
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.deleteRaidConfigurationMessageService.delete(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.updateRaidConfigurationRepository, this.eventPublisher);
	}

	@Test
	void delete_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var name = "name";
		var form = DeleteRaidConfigurationMessageForm.builder().name(name).build();
		var event = ChannelRaidEvent.builder().error("Channel not found").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.deleteRaidConfigurationMessageService.delete(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.updateRaidConfigurationRepository, this.eventPublisher);
	}

	@Test
	void delete_shouldPublishEvent_whenRaidConfigurationNotFound() {
		// Given
		var name = "name";
		var form = DeleteRaidConfigurationMessageForm.builder().name(name).build();
		var channel = Channel.builder().name(name).build();
		var event = ChannelRaidEvent.builder().error("Channel Raid Configuration not found").build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannelName(name)).willReturn(Optional.empty());
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.deleteRaidConfigurationMessageService.delete(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.findRaidConfigurationRepository).should().findByChannelName(name);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.updateRaidConfigurationRepository, this.eventPublisher);
	}

	@Test
	void delete_shouldPublishEvent_whenRaidConfigurationFound() {
		// Given
		var name = "name";
		var message = "my message";
		var form = DeleteRaidConfigurationMessageForm.builder().name(name).message(message).build();
		var channel = Channel.builder().name(name).build();
		var messages = new ArrayList<String>();
		messages.add(message);
		messages.add("another message");
		var raidConfiguration = RaidConfiguration.builder().messages(messages).build();
		var event = RaidConfigurationUpdatedEvent.builder().raidConfiguration(raidConfiguration).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		given(this.findRaidConfigurationRepository.findByChannelName(name)).willReturn(Optional.of(raidConfiguration));
		doNothing().when(this.eventPublisher).publish(event);

		// When
		this.deleteRaidConfigurationMessageService.delete(form);

		// Then
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(name);
		then(this.findRaidConfigurationRepository).should().findByChannelName(name);
		then(this.updateRaidConfigurationRepository).should().update(raidConfiguration);
		then(this.eventPublisher).should().publish(event);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.findRaidConfigurationRepository, this.updateRaidConfigurationRepository, this.eventPublisher);
	}

}
