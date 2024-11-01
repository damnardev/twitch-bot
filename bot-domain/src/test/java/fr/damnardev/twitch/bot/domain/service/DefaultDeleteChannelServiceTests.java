package fr.damnardev.twitch.bot.domain.service;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.DeleteChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.doNothing;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultDeleteChannelServiceTests {

	@InjectMocks
	private DefaultDeleteChannelService deleteChannelService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private DeleteChannelRepository deleteChannelRepository;

	@Mock
	private ChatRepository chatRepository;

	@Test
	void delete_shouldThrowException_whenChannelNotFound() {
		// Given
		var name = "name";
		var form = DeleteChannelForm.builder().name(name).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());

		// When
		var result = assertThatThrownBy(() -> this.deleteChannelService.delete(form));

		// Then
		then(this.findChannelRepository).should().findByName(name);
		verifyNoMoreInteractions(this.findChannelRepository, this.deleteChannelRepository, this.chatRepository);

		result.isInstanceOf(BusinessException.class).hasMessage("Channel not found");
	}

	@Test
	void delete_shouldDeleteChannel_whenChannelFound() {
		// Given
		var name = "name";
		var form = DeleteChannelForm.builder().name(name).build();
		var channel = Channel.builder().id(1L).name(name).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		doNothing().when(this.deleteChannelRepository).delete(channel);

		// When
		this.deleteChannelService.delete(form);

		// Then
		then(this.findChannelRepository).should().findByName(name);
		then(this.deleteChannelRepository).should().delete(channel);
		verifyNoMoreInteractions(this.findChannelRepository, this.deleteChannelRepository, this.chatRepository);
	}

	@Test
	void delete_shouldDeleteChannelAndLeave_whenChannelFoundAndEnabled() {
		// Given
		var name = "name";
		var form = DeleteChannelForm.builder().name(name).build();
		var channel = Channel.builder().id(1L).name(name).enabled(true).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		doNothing().when(this.deleteChannelRepository).delete(channel);
		doNothing().when(this.chatRepository).leave(channel);

		// When
		this.deleteChannelService.delete(form);

		// Then
		then(this.findChannelRepository).should().findByName(name);
		then(this.deleteChannelRepository).should().delete(channel);
		then(this.chatRepository).should().leave(channel);
		verifyNoMoreInteractions(this.findChannelRepository, this.deleteChannelRepository, this.chatRepository);
	}

}
