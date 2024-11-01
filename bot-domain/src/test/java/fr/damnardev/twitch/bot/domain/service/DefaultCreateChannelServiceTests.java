package fr.damnardev.twitch.bot.domain.service;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.secondary.CreateChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
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
class DefaultCreateChannelServiceTests {

	@InjectMocks
	private DefaultCreateChannelService createChannelService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private CreateChannelRepository createChannelRepository;

	@Test
	void save_shouldThrowException_whenChannelAlreadyExist() {
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
	void save_shouldCreateAndReturnChannel_whenChannelNotExist() {
		// Given
		var name = "name";
		var form = CreateChannelForm.builder().name(name).build();
		var channel = Channel.builder().name(name).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());
		given(this.createChannelRepository.save(channel)).willReturn(channel);

		// When
		var result = this.createChannelService.save(form);

		// Then
		then(this.findChannelRepository).should().findByName(name);
		then(this.createChannelRepository).should().save(channel);
		verifyNoMoreInteractions(this.findChannelRepository, this.createChannelRepository);

		var expected = Channel.builder().name(name).build();
		assertThat(result).isEqualTo(expected);
	}

}
