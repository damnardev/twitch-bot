package fr.damnardev.twitch.bot.domain.service;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.secondary.ICreateChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class CreateChannelServiceTests {

	@InjectMocks
	private CreateChannelService createChannelService;

	@Mock
	private IFindChannelRepository findChannelRepository;

	@Mock
	private ICreateChannelRepository createChannelRepository;

	@Test
	void save_channelAlreadyExists() {
		// Given
		var name = "name";
		var form = CreateChannelForm.builder().name(name).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(Channel.builder().build()));

		// When
		var result = assertThatThrownBy(() -> this.createChannelService.save(form));

		// Then
		then(this.findChannelRepository).should().findByName(name);
		verifyNoMoreInteractions(this.findChannelRepository, this.createChannelRepository);

		result.isInstanceOf(BusinessException.class).hasMessage("Channel already exists");
	}

	@Test
	void save_channelDoesNotExist() {
		// Given
		var name = "name";
		var form = CreateChannelForm.builder().name(name).build();
		var expected = Channel.builder().build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());
		given(this.createChannelRepository.save(form)).willReturn(expected);

		// When
		var result = this.createChannelService.save(form);

		// Then
		then(this.findChannelRepository).should().findByName(name);
		then(this.createChannelRepository).should().save(form);
		verifyNoMoreInteractions(this.findChannelRepository, this.createChannelRepository);

		assertThat(result).isEqualTo(expected);
	}

}
