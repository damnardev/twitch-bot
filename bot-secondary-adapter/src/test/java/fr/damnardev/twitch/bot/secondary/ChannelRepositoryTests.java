package fr.damnardev.twitch.bot.secondary;

import java.util.Arrays;
import java.util.Collections;

import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.secondary.adapter.ChannelRepository;
import fr.damnardev.twitch.bot.secondary.mapper.ChannelMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class ChannelRepositoryTests {

	@InjectMocks
	private ChannelRepository channelRepository;

	@Spy
	private ChannelMapper channelMapper;

	@Mock
	private DbChannelRepository dbChannelRepository;

	@Test
	void findAllEnabled_shouldReturnEmptyList() {
		// Given
		given(this.dbChannelRepository.findAllEnabled()).willReturn(Collections.emptyList());

		// When
		var result = this.channelRepository.findAllEnabled();

		// Then
		then(this.dbChannelRepository).should().findAllEnabled();

		verifyNoMoreInteractions(this.dbChannelRepository, this.channelMapper);

		assertThat(result).isEmpty();
	}

	@Test
	void findAllEnabled_shouldReturnListOfChannel() {
		// Given
		var dbChannel_01 = DbChannel.builder().id(1L).name("channel_01").enabled(true).online(true).build();
		var dbChannel_02 = DbChannel.builder().id(2L).name("channel_02").enabled(false).online(false).build();
		given(this.dbChannelRepository.findAllEnabled()).willReturn(Arrays.asList(dbChannel_01, dbChannel_02));

		// When
		var channels = this.channelRepository.findAllEnabled();

		// Then
		then(this.dbChannelRepository).should().findAllEnabled();
		then(this.channelMapper).should().toModel(dbChannel_01);
		then(this.channelMapper).should().toModel(dbChannel_02);

		verifyNoMoreInteractions(this.dbChannelRepository, this.channelMapper);

		var channel_01 = Channel.builder().id(1L).name("channel_01").enabled(true).online(true).build();
		var channel_02 = Channel.builder().id(2L).name("channel_02").enabled(false).online(false).build();

		assertThat(channels).isNotNull().hasSize(2).contains(channel_01, channel_02);
	}

}
