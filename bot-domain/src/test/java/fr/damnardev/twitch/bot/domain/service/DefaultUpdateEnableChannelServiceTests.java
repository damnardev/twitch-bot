package fr.damnardev.twitch.bot.domain.service;

import java.util.Optional;

import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelEnabledForm;
import fr.damnardev.twitch.bot.domain.port.secondary.ChatRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateChannelRepository;
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
class DefaultUpdateEnableChannelServiceTests {

	@InjectMocks
	private DefaultUpdateEnableChannelService updateEnableChannelService;

	@Mock
	private FindChannelRepository findChannelRepository;

	@Mock
	private UpdateChannelRepository updateChannelRepository;

	@Mock
	private ChatRepository chatRepository;

	@Test
	void updateEnabled_shouldThrowException_whenChannelNotFound() {
		// Given
		var name = "name";
		var form = UpdateChannelEnabledForm.builder().name(name).enabled(true).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.empty());

		// When
		var result = assertThatThrownBy(() -> this.updateEnableChannelService.updateEnabled(form));

		// Then
		then(this.findChannelRepository).should().findByName(name);
		verifyNoMoreInteractions(this.findChannelRepository, this.updateChannelRepository, this.chatRepository);

		result.isInstanceOf(BusinessException.class).hasMessage("Channel not found");
	}

	@Test
	void updateEnabled_shouldUpdateChannelAndJoin_whenEnabledIsTrue() {
		// Given
		var name = "name";
		var form = UpdateChannelEnabledForm.builder().name(name).enabled(true).build();
		var channel = Channel.builder().id(1L).name(name).enabled(false).online(true).build();
		var updatedChannel = channel.toBuilder().enabled(true).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		doNothing().when(this.updateChannelRepository).update(updatedChannel);

		// When
		this.updateEnableChannelService.updateEnabled(form);

		// Then
		then(this.findChannelRepository).should().findByName(name);
		then(this.updateChannelRepository).should().update(updatedChannel);
		then(this.chatRepository).should().join(updatedChannel);
		verifyNoMoreInteractions(this.findChannelRepository, this.updateChannelRepository, this.chatRepository);
	}

	@Test
	void updateEnabled_shouldUpdateChannelAndLeave_whenEnabledIsFalse() {
		// Given
		var name = "name";
		var form = UpdateChannelEnabledForm.builder().name(name).enabled(false).build();
		var channel = Channel.builder().id(1L).name(name).enabled(true).online(true).build();
		var updatedChannel = channel.toBuilder().enabled(false).build();

		given(this.findChannelRepository.findByName(name)).willReturn(Optional.of(channel));
		doNothing().when(this.updateChannelRepository).update(updatedChannel);

		// When
		this.updateEnableChannelService.updateEnabled(form);

		// Then
		then(this.findChannelRepository).should().findByName(name);
		then(this.updateChannelRepository).should().update(updatedChannel);
		then(this.chatRepository).should().leave(updatedChannel);
		verifyNoMoreInteractions(this.findChannelRepository, this.updateChannelRepository, this.chatRepository);
	}

}
