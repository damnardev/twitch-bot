package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.List;

import fr.damnardev.twitch.bot.database.entity.Channel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.model.User;
import fr.damnardev.twitch.bot.domain.port.secondary.IFindAllChannelRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class FindAllChannelRepository implements IFindAllChannelRepository {

	private final DbChannelRepository dbChannelRepository;

	@Override
	@Transactional(readOnly = true)
	public List<ChannelInfo> findAll() {
		return this.dbChannelRepository.findAll().stream().map(this::toModel).toList();
	}

	private ChannelInfo toModel(Channel channel) {
		var user = User.builder().id(channel.getId()).name(channel.getName()).build();
		return ChannelInfo.builder().user(user).enabled(channel.isBotEnabled()).online(channel.isOnline()).build();
	}

}
