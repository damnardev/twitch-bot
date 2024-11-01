package fr.damnardev.twitch.bot.secondary.adapter;

import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.Channel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.mockito.BDDMockito.doNothing;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultDeleteChannelRepositoryTest {

	@InjectMocks
	private DefaultDeleteChannelRepository deleteChannelRepository;

	@Mock
	private DbChannelRepository dbChannelRepository;

	@Test
	void delete_shouldInvokeDelete() {
		// Given
		var channelId = 1L;
		var channel = Channel.builder().id(channelId).build();

		doNothing().when(this.dbChannelRepository).deleteById(channelId);

		// When
		this.deleteChannelRepository.delete(channel);

		// Then
		then(this.dbChannelRepository).should().deleteById(channelId);
		verifyNoMoreInteractions(this.dbChannelRepository);
	}

}
