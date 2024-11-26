package fr.damnardev.twitch.bot.core.service;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import fr.damnardev.twitch.bot.core.service.command.CommandInterpreter;
import fr.damnardev.twitch.bot.exception.BusinessException;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;
import fr.damnardev.twitch.bot.model.CommandType;
import fr.damnardev.twitch.bot.model.event.ErrorEvent;
import fr.damnardev.twitch.bot.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.port.secondary.command.FindChannelCommandRepository;
import fr.damnardev.twitch.bot.port.secondary.command.UpdateChannelCommandRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultChannelMessageEventServiceTests {

	private DefaultChannelMessageEventService channelMessageEventService;

	private DefaultTryService tryService;

	@Mock
	private EventPublisher eventPublisher;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private FindChannelCommandRepository findChannelCommandRepository;

	@Mock
	private UpdateChannelCommandRepository updateChannelCommandRepository;

	@Mock
	private CommandInterpreter commandInterpreter;

	@BeforeEach
	void setUp() {
		this.tryService = new DefaultTryService(this.eventPublisher);
		this.tryService = spy(this.tryService);
		given(this.commandInterpreter.getCommandTypeInterpreter()).willReturn(CommandType.TEXT);
		var commandInterpreters = Collections.singletonList(this.commandInterpreter);
		this.channelMessageEventService = new DefaultChannelMessageEventService(this.tryService, this.findChannelRepository, this.findChannelCommandRepository, this.updateChannelCommandRepository, commandInterpreters);
	}

	@Test
	void process_shouldPublishEvent_whenChannelNotFound() {
		// Given
		var channelName = "channelName";
		var form = ChannelMessageEventForm.builder().channelName(channelName).build();
		var captor = ArgumentCaptor.forClass(ErrorEvent.class);

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.empty());

		// When
		this.channelMessageEventService.process(form);

		// Then
		then(this.commandInterpreter).should().getCommandTypeInterpreter();
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.eventPublisher).should().publish(captor.capture());
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher, this.findChannelCommandRepository, this.updateChannelCommandRepository, this.commandInterpreter);

		var event = captor.getValue();
		assertThat(event.getException()).isNotNull().isInstanceOf(BusinessException.class).hasMessage("Channel not found");
	}

	@Test
	void process_shouldDoNothing_whenChannelFoundAndMessageNotStartByToken() {
		// Given
		var channelName = "channelName";
		var channel = Channel.builder().name(channelName).build();
		var form = ChannelMessageEventForm.builder().channelName(channelName).message("").build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));

		// When
		this.channelMessageEventService.process(form);

		// Then
		then(this.commandInterpreter).should().getCommandTypeInterpreter();
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher, this.findChannelCommandRepository, this.updateChannelCommandRepository, this.commandInterpreter);
	}

	@Test
	void process_shouldDoNothing_whenChannelFoundAndCommandIsEmpty() {
		// Given
		var channelName = "channelName";
		var channel = Channel.builder().name(channelName).build();
		var form = ChannelMessageEventForm.builder().channelName(channelName).message("!foo").build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findChannelCommandRepository.findByChannel(channel)).willReturn(Collections.emptyMap());


		// When
		this.channelMessageEventService.process(form);

		// Then
		then(this.commandInterpreter).should().getCommandTypeInterpreter();
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findChannelCommandRepository).should().findByChannel(channel);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher, this.findChannelCommandRepository, this.updateChannelCommandRepository, this.commandInterpreter);
	}

	@Test
	void process_shouldDoNothing_whenChannelFoundAndCommandNotFound() {
		// Given
		var channelName = "channelName";
		var channel = Channel.builder().name(channelName).build();
		var command = Command.builder().build();
		var form = ChannelMessageEventForm.builder().channelName(channelName).message("!foo").build();


		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findChannelCommandRepository.findByChannel(channel)).willReturn(Map.of("!bar", command));
		// When
		this.channelMessageEventService.process(form);

		// Then
		then(this.commandInterpreter).should().getCommandTypeInterpreter();
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findChannelCommandRepository).should().findByChannel(channel);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher, this.findChannelCommandRepository, this.updateChannelCommandRepository, this.commandInterpreter);
	}

	@Test
	void process_shouldDoNothing_whenChannelFoundAndCommandFoundButNoValidTime() {
		// Given
		var channelName = "channelName";
		var channel = Channel.builder().name(channelName).build();
		var command = Command.builder().lastExecution(OffsetDateTime.now()).cooldown(60).build();
		var form = ChannelMessageEventForm.builder().channelName(channelName).message("!foo").build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findChannelCommandRepository.findByChannel(channel)).willReturn(Map.of("!bar", command));
		// When
		this.channelMessageEventService.process(form);

		// Then
		then(this.commandInterpreter).should().getCommandTypeInterpreter();
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findChannelCommandRepository).should().findByChannel(channel);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher, this.findChannelCommandRepository, this.updateChannelCommandRepository, this.commandInterpreter);
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void process_shouldDoNothing_whenChannelFoundAndCommandFoundButNoInterpreter(boolean hasLastExecution) {
		// Given
		var channelName = "channelName";
		var channel = Channel.builder().name(channelName).build();
		var command = hasLastExecution ? Command.builder().lastExecution(OffsetDateTime.now().minusHours(1)).cooldown(60).build() : Command.builder().build();
		var form = ChannelMessageEventForm.builder().channelName(channelName).message("!foo").build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findChannelCommandRepository.findByChannel(channel)).willReturn(Map.of("foo", command));

		// When
		this.channelMessageEventService.process(form);

		// Then
		then(this.commandInterpreter).should().getCommandTypeInterpreter();
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findChannelCommandRepository).should().findByChannel(channel);
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher, this.findChannelCommandRepository, this.updateChannelCommandRepository, this.commandInterpreter);
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void process_shouldDoNothing_whenChannelFoundAndCommandFoundAndInterpreter(boolean hasLastExecution) {
		// Given
		var channelName = "channelName";
		var channel = Channel.builder().name(channelName).build();
		var command = hasLastExecution ? Command.builder().type(CommandType.TEXT).lastExecution(OffsetDateTime.now().minusHours(1)).cooldown(60).build() : Command.builder().type(CommandType.TEXT).build();
		var form = ChannelMessageEventForm.builder().channelName(channelName).message("!foo").build();
		var formWithoutToken = ChannelMessageEventForm.builder().channelName(channelName).message("foo").build();

		given(this.findChannelRepository.findByName(channelName)).willReturn(Optional.of(channel));
		given(this.findChannelCommandRepository.findByChannel(channel)).willReturn(Map.of("foo", command));

		// When
		this.channelMessageEventService.process(form);

		// Then
		then(this.commandInterpreter).should().getCommandTypeInterpreter();
		then(this.tryService).should().doTry(any(), eq(form));
		then(this.findChannelRepository).should().findByName(channelName);
		then(this.findChannelCommandRepository).should().findByChannel(channel);
		then(this.commandInterpreter).should().interpret(channel, command, formWithoutToken);
		then(this.updateChannelCommandRepository).should().update(command.toBuilder().lastExecution(any()).build());
		verifyNoMoreInteractions(this.tryService, this.findChannelRepository, this.eventPublisher, this.findChannelCommandRepository, this.updateChannelCommandRepository, this.commandInterpreter);
	}

}
