package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.Channel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindChannelRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FindChannelRepository implements IFindChannelRepository {

	private final DbChannelRepository dbChannelRepository;

	@Override
	@Transactional(readOnly = true)
	public Optional<ChannelInfo> find(User channel) {
		return this.dbChannelRepository.findByName(channel.name()).map(this::toModel);
	}

	private ChannelInfo toModel(Channel channel) {
		var user = User.builder().id(channel.getId()).name(channel.getName()).build();
		return ChannelInfo.builder().user(user).enabled(channel.isBotEnabled()).online(channel.isOnline()).build();
	}

}
