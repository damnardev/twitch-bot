package fr.damnardev.twitch.bot.core.service.command;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;
import fr.damnardev.twitch.bot.model.CommandType;
import fr.damnardev.twitch.bot.model.Message;
import fr.damnardev.twitch.bot.port.primary.DateService;
import fr.damnardev.twitch.bot.port.secondary.MessageRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class PrevNoseInterpreterTests {

	private static final ZoneId zoneId = ZoneId.of("Europe/Paris");

	@InjectMocks
	private PrevNoseInterpreter prevNoseInterpreter;

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private DateService dateService;

	@Test
	void getCommandTypeInterpreter_shouldReturnNextNose() {
		// When
		var commandType = this.prevNoseInterpreter.getCommandTypeInterpreter();

		// Then
		assertThat(commandType).isEqualTo(CommandType.PREV_NOSE);
	}

	@Test
	void interpret_shouldSendNextNoseMessage() {
		// Given
		var channelId = 1L;
		var channelName = "channelName";
		var channel = Channel.builder().id(channelId).name(channelName).build();
		var command = Command.builder().cooldown(60).build();
		var now = OffsetDateTime.of(2021, 1, 1, 12, 0, 0, 0, ZoneOffset.of("Z"));
		var message = Message.builder().channelId(channelId).channelName(channelName).content("Le nez précédent remonte à 49 minutes 2021-01-01 11:11 [⏰ 60 s]").build();

		given(this.dateService.now(zoneId)).willReturn(now);

		// When
		this.prevNoseInterpreter.interpret(channel, command, null);

		// Then
		then(this.dateService).should().now(zoneId);
		then(this.messageRepository).should().sendMessage(message);
		verifyNoMoreInteractions(this.messageRepository, this.dateService);
	}

	@Test
	void interpret_shouldSendNextNoseMessage_whenMinutesGreaterThanHour() {
		// Given
		var channelId = 1L;
		var channelName = "channelName";
		var channel = Channel.builder().id(channelId).name(channelName).build();
		var command = Command.builder().cooldown(60).build();
		var now = OffsetDateTime.of(2021, 1, 1, 12, 13, 0, 0, ZoneOffset.of("Z"));
		var message = Message.builder().channelId(channelId).channelName(channelName).content("Le nez précédent remonte à 1 minutes 2021-01-01 12:12 [⏰ 60 s]").build();

		given(this.dateService.now(zoneId)).willReturn(now);

		// When
		this.prevNoseInterpreter.interpret(channel, command, null);

		// Then
		then(this.dateService).should().now(zoneId);
		then(this.messageRepository).should().sendMessage(message);
		verifyNoMoreInteractions(this.messageRepository, this.dateService);
	}

	@Test
	void interpret_shouldSendNextNoseMessage_whenMinutesEqualsHour() {
		// Given
		var channelId = 1L;
		var channelName = "channelName";
		var channel = Channel.builder().id(channelId).name(channelName).build();
		var command = Command.builder().cooldown(60).build();
		var now = OffsetDateTime.of(2021, 1, 1, 12, 12, 0, 0, ZoneOffset.of("Z"));
		var message = Message.builder().channelId(channelId).channelName(channelName).content("Le nez précédent remonte à 0 minutes 2021-01-01 12:12 [⏰ 60 s]").build();

		given(this.dateService.now(zoneId)).willReturn(now);

		// When
		this.prevNoseInterpreter.interpret(channel, command, null);

		// Then
		then(this.dateService).should().now(zoneId);
		then(this.messageRepository).should().sendMessage(message);
		verifyNoMoreInteractions(this.messageRepository, this.dateService);
	}

}
