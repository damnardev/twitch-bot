package fr.damnardev.twitch.bot.secondary;

import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.secondary.adapter.DefaultUpdateChannelRepository;
import fr.damnardev.twitch.bot.secondary.mapper.ChannelMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultUpdateChannelRepositoryTests {

	@InjectMocks
	private DefaultUpdateChannelRepository updateChannelRepository;

	@Mock
	private DbChannelRepository dbChannelRepository;

	@Spy
	private ChannelMapper channelMapper;

	@Test
	void update_shouldInvokeSave_whenChannelIsUpdated() {
		// Given
		var channel = Channel.builder().id(1L).name("name").online(true).enabled(true).build();
		var dbChannel = DbChannel.builder().id(1L).name("name").online(true).enabled(true).build();

		given(this.dbChannelRepository.save(dbChannel)).willReturn(dbChannel);

		// When
		this.updateChannelRepository.update(channel);

		// Then
		then(this.channelMapper).should().toEntity(channel);
		then(this.dbChannelRepository).should().save(dbChannel);
		verifyNoMoreInteractions(this.channelMapper, this.dbChannelRepository);
	}

}
