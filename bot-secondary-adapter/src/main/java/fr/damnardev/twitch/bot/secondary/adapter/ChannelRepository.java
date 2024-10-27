package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.List;

import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;
import fr.damnardev.twitch.bot.secondary.mapper.ChannelMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChannelRepository implements IChannelRepository {

	private final DbChannelRepository dbChannelRepository;

	private final ChannelMapper channelMapper;

	@Override
	@Transactional(readOnly = true)
	public List<Channel> findAllEnabled() {
		return this.dbChannelRepository.findAllEnabled().stream().map(this.channelMapper::toModel).toList();
	}

}
