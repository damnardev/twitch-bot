package fr.damnardev.twitch.bot.secondary;

import java.util.Collections;

import com.github.twitch4j.helix.TwitchHelix;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import com.netflix.hystrix.HystrixCommand;
import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.secondary.adapter.CreateChannelRepository;
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
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class CreateChannelRepositoryTests {

	@InjectMocks
	private CreateChannelRepository createChannelRepository;

	@Mock
	private DbChannelRepository dbChannelRepository;

	@Mock
	private TwitchHelix twitchHelix;

	@Spy
	private ChannelMapper channelMapper;

	@Test
	void save_shouldReturnChannel() {
		// Given
		var name = "name";
		var form = CreateChannelForm.builder().name(name).build();

		var hystrixCommand = mock(HystrixCommand.class);
		var userList = mock(UserList.class);
		var user = mock(User.class);

		var dbChannel = DbChannel.builder().id(1L).name(name).build();

		given(this.twitchHelix.getUsers(null, null, Collections.singletonList(name))).willReturn(hystrixCommand);
		given(hystrixCommand.execute()).willReturn(userList);
		given(userList.getUsers()).willReturn(Collections.singletonList(user));
		given(user.getId()).willReturn("1");
		given(this.dbChannelRepository.save(dbChannel)).willReturn(dbChannel);

		// When
		var result = this.createChannelRepository.save(form);

		// Then
		then(this.twitchHelix).should().getUsers(null, null, Collections.singletonList(name));
		then(hystrixCommand).should().execute();
		then(userList).should().getUsers();
		then(user).should().getId();
		then(this.channelMapper).should().toModel(dbChannel);
		then(this.dbChannelRepository).should().save(dbChannel);
		then(this.channelMapper).should().toModel(dbChannel);
		verifyNoMoreInteractions(this.twitchHelix, hystrixCommand, userList, user, this.dbChannelRepository, this.channelMapper);

		var expected = Channel.builder().build().toBuilder().id(1L).name(name).build();

		assertThat(result).isEqualTo(expected);
	}

}
