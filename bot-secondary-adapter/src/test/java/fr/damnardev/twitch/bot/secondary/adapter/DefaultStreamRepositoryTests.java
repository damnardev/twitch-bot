package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.netflix.hystrix.HystrixCommand;
import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.exception.FatalException;
import fr.damnardev.twitch.bot.domain.model.Channel;
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
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultStreamRepositoryTests {

	@InjectMocks
	private DefaultStreamRepository streamRepository;

	@Mock
	private DbChannelRepository dbChannelRepository;

	@Mock
	private TwitchHelix twitchHelix;

	@Spy
	private ChannelMapper channelMapper;

	@Test
	void computeAll_shouldReturnEmptyList_whenNoChannelsProvided() {
		// Given
		var channels = Collections.<Channel>emptyList();

		// When
		var result = this.streamRepository.computeAll(channels);

		// Then
		verifyNoMoreInteractions(this.dbChannelRepository, this.twitchHelix, this.channelMapper);

		assertThat(result).isNotNull().isEmpty();
	}

	@Test
	void computeAll_shouldUpdateStatus_whenChannelsProvided() {
		// Given
		var channel_01 = Channel.builder().id(1L).build();
		var channel_02 = Channel.builder().id(2L).build();
		var dbChannel_01 = DbChannel.builder().id(1L).name("channel_01").enabled(true).online(true).build();
		var dbChannel_02 = DbChannel.builder().id(2L).name("channel_02").enabled(true).build();
		var names = Arrays.asList("channel_01", "channel_02");
		var hystrixCommand = mock(HystrixCommand.class);
		var streamList = mock(StreamList.class);
		var stream_02 = mock(Stream.class);

		given(this.dbChannelRepository.findAllById(Arrays.asList(1L, 2L))).willReturn(Arrays.asList(dbChannel_01, dbChannel_02));
		given(this.twitchHelix.getStreams(null, null, null, null, null, null, null, names)).willReturn(hystrixCommand);
		given(hystrixCommand.execute()).willReturn(streamList);
		given(streamList.getStreams()).willReturn(Collections.singletonList(stream_02));
		given(stream_02.getUserLogin()).willReturn("channel_02");
		given(stream_02.getType()).willReturn("live");
		given(this.dbChannelRepository.saveAllAndFlush(Arrays.asList(dbChannel_01, dbChannel_02))).willReturn(Arrays.asList(dbChannel_01, dbChannel_02));

		// When
		var result = this.streamRepository.computeAll(Arrays.asList(channel_01, channel_02));

		// Then
		then(this.dbChannelRepository).should().findAllById(Arrays.asList(1L, 2L));
		then(this.twitchHelix).should().getStreams(null, null, null, null, null, null, null, names);
		then(hystrixCommand).should().execute();
		then(streamList).should().getStreams();
		then(stream_02).should().getUserLogin();
		then(stream_02).should().getType();
		then(this.dbChannelRepository).should().saveAllAndFlush(Arrays.asList(dbChannel_01, dbChannel_02));
		then(this.channelMapper).should().toModel(dbChannel_01);
		then(this.channelMapper).should().toModel(dbChannel_02);
		verifyNoMoreInteractions(this.dbChannelRepository, this.twitchHelix, this.channelMapper, hystrixCommand, streamList, stream_02);

		var expectedChannel_01 = Channel.builder().id(1L).name("channel_01").enabled(true).online(false).build();
		var expectedChannel_02 = Channel.builder().id(2L).name("channel_02").enabled(true).online(true).build();
		assertThat(result).isNotNull().hasSize(2).usingRecursiveFieldByFieldElementComparatorIgnoringFields("commands", "raids").contains(expectedChannel_01, expectedChannel_02);
	}

	@Test
	void compute_shouldThrowException_whenChannelNotFound() {
		// Given
		var channel = Channel.builder().id(1L).build();

		given(this.dbChannelRepository.findById(1L)).willReturn(Optional.empty());

		// When
		var result = assertThatThrownBy(() -> this.streamRepository.compute(channel));

		// Then
		then(this.dbChannelRepository).should().findById(1L);
		verifyNoMoreInteractions(this.dbChannelRepository, this.twitchHelix, this.channelMapper);

		result.isInstanceOf(FatalException.class).hasMessage("Channel not found");
	}

	@Test
	void compute_shouldUpdateStatus_whenCalled() {
		// Given
		var channel = Channel.builder().id(1L).build();
		var dbChannel = DbChannel.builder().id(1L).name("channel").enabled(true).online(true).build();
		var names = Collections.singletonList("channel");
		var hystrixCommand = mock(HystrixCommand.class);
		var streamList = mock(StreamList.class);
		var stream = mock(Stream.class);

		given(this.dbChannelRepository.findById(1L)).willReturn(Optional.of(dbChannel));
		given(this.twitchHelix.getStreams(null, null, null, null, null, null, null, names)).willReturn(hystrixCommand);
		given(hystrixCommand.execute()).willReturn(streamList);
		given(streamList.getStreams()).willReturn(Collections.singletonList(stream));
		given(stream.getUserLogin()).willReturn("channel_02");
		given(stream.getType()).willReturn("live");
		given(this.dbChannelRepository.saveAndFlush(dbChannel)).willReturn(dbChannel);

		// When
		var result = this.streamRepository.compute(channel);

		// Then
		then(this.dbChannelRepository).should().findById(1L);
		then(this.twitchHelix).should().getStreams(null, null, null, null, null, null, null, names);
		then(hystrixCommand).should().execute();
		then(streamList).should().getStreams();
		then(stream).should().getUserLogin();
		then(stream).should().getType();
		then(this.dbChannelRepository).should().saveAndFlush(dbChannel);
		then(this.channelMapper).should().toModel(dbChannel);
		verifyNoMoreInteractions(this.dbChannelRepository, this.twitchHelix, this.channelMapper, hystrixCommand, streamList, stream);

		var expected = Channel.builder().id(1L).name("channel").enabled(true).online(false).build();
		assertThat(result).isNotNull().isEqualTo(expected);
	}

}
