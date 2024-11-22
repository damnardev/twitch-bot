package fr.damnardev.twitch.bot.secondary.adapter.command;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.entity.DbChannelCommand;
import fr.damnardev.twitch.bot.database.repository.DbChannelCommandRepository;
import fr.damnardev.twitch.bot.domain.model.Command;
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
import static org.mockito.Mockito.spy;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultUpdateChannelCommandRepositoryTests {

	@InjectMocks
	private DefaultUpdateChannelCommandRepository updateChannelCommandRepository;

	@Mock
	private DbChannelCommandRepository dbChannelCommandRepository;

	@Test
	void update_shouldNotUpdate_whenCommandDoesNotExist() {
		// Given
		var channelName = "channelName";
		var name = "!foo";
		var command = Command.builder().channelName(channelName).name(name).build();

		given(this.dbChannelCommandRepository.findByChannelNameAndName(channelName, name)).willReturn(Optional.empty());

		// When
		this.updateChannelCommandRepository.update(command);

		// Then
		then(this.dbChannelCommandRepository).should().findByChannelNameAndName(channelName, name);
		verifyNoMoreInteractions(this.dbChannelCommandRepository);
	}

	@Test
	void update_shouldUpdate_whenCommandExists() {
		// Given
		var channelName = "channelName";
		var name = "!foo";
		var messages = Collections.singletonList("message1");
		var command = Command.builder().channelName(channelName).name(name).enabled(true)
				.cooldown(60).lastExecution(OffsetDateTime.now()).messages(messages).build();
		var dbMessages = spy(new ArrayList<String>());
		var dbChannelCommand = DbChannelCommand.builder().id(1L).
				channel(DbChannel.builder().name(channelName).build())
				.name(name).enabled(false).cooldown(30).lastExecution(null).messages(dbMessages).build();

		given(this.dbChannelCommandRepository.findByChannelNameAndName(channelName, name)).willReturn(Optional.of(dbChannelCommand));

		// When
		this.updateChannelCommandRepository.update(command);

		// Then
		then(this.dbChannelCommandRepository).should().findByChannelNameAndName(channelName, name);
		then(this.dbChannelCommandRepository).should().save(dbChannelCommand);
		then(dbMessages).should().clear();
		then(dbMessages).should().addAll(messages);
		verifyNoMoreInteractions(this.dbChannelCommandRepository, dbMessages);

		var expected = DbChannelCommand.builder().id(1L).
				channel(DbChannel.builder().name(channelName).build())
				.name(name).enabled(true).cooldown(60).lastExecution(command.lastExecution()).messages(messages).build();
		assertThat(dbChannelCommand)
				.usingRecursiveComparison().ignoringFields("channel")
				.isEqualTo(expected);
	}

}
