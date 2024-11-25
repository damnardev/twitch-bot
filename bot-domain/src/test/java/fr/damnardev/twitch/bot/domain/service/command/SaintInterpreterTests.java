package fr.damnardev.twitch.bot.domain.service.command;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.Command;
import fr.damnardev.twitch.bot.domain.model.Message;
import fr.damnardev.twitch.bot.domain.model.form.ChannelMessageEventForm;
import fr.damnardev.twitch.bot.domain.port.secondary.MessageRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.SaintRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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
class SaintInterpreterTests {

	@InjectMocks
	private SaintInterpreter saintInterpreter;

	@Mock
	private MessageRepository messageRepository;

	@Mock
	private SaintRepository saintRepository;

	@ParameterizedTest
	@ValueSource(strings = { "saint", "saintss", "" })
	void canInterpret_shouldReturnFalse_whenCommandIsNotSaints(String source) {
		// Given
		var command = Command.builder().name("saint").build();

		// When
		var result = this.saintInterpreter.canInterpret(null, command, null);

		// Then
		verifyNoMoreInteractions(this.messageRepository, this.saintRepository);

		assertThat(result).isFalse();
	}

	@Test
	void canInterpret_shouldReturnTrue_whenCommandIsSaints() {
		// Given
		var command = Command.builder().name("saints").build();

		// When
		var result = this.saintInterpreter.canInterpret(null, command, null);

		// Then
		verifyNoMoreInteractions(this.messageRepository, this.saintRepository);

		assertThat(result).isTrue();
	}

	@Test
	void interpret_shouldDoNothing_whenSaintIsNotFound() {
		// Given
		var channel = Channel.builder().id(1L).name("channel_name").build();
		var command = Command.builder().name("saints").build();
		var form = ChannelMessageEventForm.builder().build();

		given(this.saintRepository.find()).willReturn(Optional.empty());

		// When
		this.saintInterpreter.interpret(channel, command, form);

		// Then
		then(this.saintRepository).should().find();
		verifyNoMoreInteractions(this.messageRepository, this.saintRepository);
	}


	@Test
	void interpret_shouldSendMessage_whenSaintIsFound() {
		// Given
		var channel = Channel.builder().id(1L).name("channel_name").build();
		var command = Command.builder().name("saints").build();
		var form = ChannelMessageEventForm.builder().build();
		var value = "saint_value";
		var message = Message.builder().channelId(channel.id()).channelName(channel.name()).content(value).build();

		given(this.saintRepository.find()).willReturn(Optional.of(value));

		// When
		this.saintInterpreter.interpret(channel, command, form);

		// Then
		then(this.saintRepository).should().find();
		then(this.messageRepository).should().sendMessage(message);

		verifyNoMoreInteractions(this.messageRepository, this.saintRepository);
	}

}
