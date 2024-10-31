package fr.damnardev.twitch.bot.domain.service;

import java.util.Collections;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
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
class FindAllChannelServiceTests {

	@InjectMocks
	private FindAllChannelService findAllChannelService;

	@Mock
	private IFindChannelRepository findChannelRepository;

	@Test
	void findAll_shouldReturnSameTable() {
		// Given
		var channels = Collections.singletonList(Channel.builder().build());
		given(this.findChannelRepository.findAll()).willReturn(channels);

		// When
		var result = this.findAllChannelService.findAll();

		// Then
		then(this.findChannelRepository).should().findAll();
		verifyNoMoreInteractions(this.findChannelRepository);

		var expected = Collections.singletonList(Channel.builder().build());
		assertThat(result).isEqualTo(expected);
	}

}
