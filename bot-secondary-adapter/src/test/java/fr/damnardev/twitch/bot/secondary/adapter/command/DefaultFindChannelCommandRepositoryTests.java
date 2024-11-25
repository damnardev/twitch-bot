package fr.damnardev.twitch.bot.secondary.adapter.command;

import java.util.Collections;

import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.entity.DbChannelCommand;
import fr.damnardev.twitch.bot.database.repository.DbChannelCommandRepository;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.Command;
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
class DefaultFindChannelCommandRepositoryTests {

	@InjectMocks
	private DefaultFindChannelCommandRepository findChannelCommandRepository;

	@Mock
	private DbChannelCommandRepository dbChannelCommandRepository;

	@Test
	void findByChannel_shouldReturnEmptyMap_whenNameNotFound() {
		// Given
		var channelName = "channelName";
		var channel = Channel.builder().name(channelName).build();

		given(this.dbChannelCommandRepository.findByChannelName(channelName)).willReturn(Collections.emptyList());

		// When
		var result = this.findChannelCommandRepository.findByChannel(channel);

		// Then
		then(this.dbChannelCommandRepository).should().findByChannelName(channelName);
		verifyNoMoreInteractions(this.dbChannelCommandRepository);

		assertThat(result).isEmpty();
	}

	@Test
	void findByChannel_shouldReturnMap_whenNameNotFound() {
		// Given
		var channelName = "channelName";
		var channel = Channel.builder().name(channelName).build();

		var message = "message";
		var dbChannelCommand = DbChannelCommand.builder().id(1L)
				.channel(DbChannel.builder().name(channelName).build())
				.name("!foo").enabled(true).cooldown(60).lastExecution(null).messages(Collections.singletonList(message)).build();

		given(this.dbChannelCommandRepository.findByChannelName(channelName)).willReturn(Collections.singletonList(dbChannelCommand));

		// When
		var result = this.findChannelCommandRepository.findByChannel(channel);

		// Then
		then(this.dbChannelCommandRepository).should().findByChannelName(channelName);
		verifyNoMoreInteractions(this.dbChannelCommandRepository);

		var expected = Command.builder().channelId(1L).channelName(channelName)
				.name("!foo").enabled(true).cooldown(60).lastExecution(null).messages(Collections.singletonList(message)).build();
		assertThat(result).isNotEmpty().containsEntry("!foo", expected);
	}

}
