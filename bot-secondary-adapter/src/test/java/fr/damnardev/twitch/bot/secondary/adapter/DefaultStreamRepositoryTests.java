package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.Arrays;
import java.util.Collections;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.Stream;
import com.github.twitch4j.helix.domain.StreamList;
import com.netflix.hystrix.HystrixCommand;
import fr.damnardev.twitch.bot.domain.model.Channel;
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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultStreamRepositoryTests {

	@InjectMocks
	private DefaultStreamRepository streamRepository;

	@Mock
	private TwitchHelix twitchHelix;

	@Test
	void computeOnlineOnline_shouldReturnEmptyList_whenNoChannelsProvided() {
		// Given
		var channels = Collections.<Channel>emptyList();

		// When
		var result = this.streamRepository.computeOnline(channels);

		// Then
		verifyNoMoreInteractions(this.twitchHelix);

		assertThat(result).isNotNull().isEmpty();
	}

	@Test
	void computeOnlineOnline_shouldUpdateStatus_whenChannelsProvided() {
		// Given
		var channel_01 = Channel.builder().id(1L).name("channel_01").build();
		var channel_02 = Channel.builder().id(2L).name("channel_02").build();
		var names = Arrays.asList("channel_01", "channel_02");
		var hystrixCommand = mock(HystrixCommand.class);
		var streamList = mock(StreamList.class);
		var stream_02 = mock(Stream.class);

		given(this.twitchHelix.getStreams(null, null, null, null, null, null, null, names)).willReturn(hystrixCommand);
		given(hystrixCommand.execute()).willReturn(streamList);
		given(streamList.getStreams()).willReturn(Collections.singletonList(stream_02));
		given(stream_02.getUserLogin()).willReturn("channel_02");
		given(stream_02.getType()).willReturn("live");

		// When
		var result = this.streamRepository.computeOnline(Arrays.asList(channel_01, channel_02));

		// Then
		then(this.twitchHelix).should().getStreams(null, null, null, null, null, null, null, names);
		then(hystrixCommand).should().execute();
		then(streamList).should().getStreams();
		then(stream_02).should().getUserLogin();
		then(stream_02).should().getType();
		verifyNoMoreInteractions(this.twitchHelix, hystrixCommand, streamList, stream_02);

		var expectedChannel_01 = Channel.builder().id(1L).name("channel_01").online(false).build();
		var expectedChannel_02 = Channel.builder().id(2L).name("channel_02").online(true).build();
		assertThat(result).isNotNull().hasSize(2).usingRecursiveFieldByFieldElementComparatorIgnoringFields("commands", "raids").contains(expectedChannel_01, expectedChannel_02);
	}

	@Test
	void computeOnline_shouldUpdateStatus_whenCalled() {
		// Given
		var channel = Channel.builder().id(1L).name("channel").build();
		var names = Collections.singletonList("channel");
		var hystrixCommand = mock(HystrixCommand.class);
		var streamList = mock(StreamList.class);
		var stream = mock(Stream.class);

		given(this.twitchHelix.getStreams(null, null, null, null, null, null, null, names)).willReturn(hystrixCommand);
		given(hystrixCommand.execute()).willReturn(streamList);
		given(streamList.getStreams()).willReturn(Collections.singletonList(stream));
		given(stream.getUserLogin()).willReturn("channel");
		given(stream.getType()).willReturn("live");

		// When
		var result = this.streamRepository.computeOnline(channel);

		// Then
		then(this.twitchHelix).should().getStreams(null, null, null, null, null, null, null, names);
		then(hystrixCommand).should().execute();
		then(streamList).should().getStreams();
		then(stream).should().getUserLogin();
		then(stream).should().getType();
		verifyNoMoreInteractions(this.twitchHelix, hystrixCommand, streamList, stream);

		var expected = Channel.builder().id(1L).name("channel").online(true).build();
		assertThat(result).isNotNull().isEqualTo(expected);
	}

}
