package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.Collections;

import com.github.twitch4j.helix.TwitchHelix;
import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.secondary.ICreateChannelRepository;
import fr.damnardev.twitch.bot.secondary.mapper.ChannelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class CreateChannelRepository implements ICreateChannelRepository {

	private final DbChannelRepository dbChannelRepository;

	private final TwitchHelix twitchHelix;

	private final ChannelMapper channelMapper;

	@Override
	public Channel save(CreateChannelForm form) {
		var user = this.twitchHelix.getUsers(null, null, Collections.singletonList(form.name())).execute().getUsers().getFirst();
		var id = Long.parseLong(user.getId());
		var dbChannel = DbChannel.builder().id(id).name(form.name()).build();
		dbChannel = this.dbChannelRepository.save(dbChannel);
		return this.channelMapper.toModel(dbChannel);
	}

}
