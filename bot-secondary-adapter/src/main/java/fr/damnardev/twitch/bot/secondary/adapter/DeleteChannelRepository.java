package fr.damnardev.twitch.bot.secondary.adapter;

import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.secondary.IDeleteChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DeleteChannelRepository implements IDeleteChannelRepository {

	private final DbChannelRepository repository;

	@Override
	@Transactional
	public void delete(ChannelInfo channelInfo) {
		log.info("Deleting channel {}", channelInfo);
		this.repository.deleteById(channelInfo.user().id());
	}

}
