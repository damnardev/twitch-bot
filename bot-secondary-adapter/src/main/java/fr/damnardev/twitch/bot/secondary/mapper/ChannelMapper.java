package fr.damnardev.twitch.bot.secondary.mapper;

import fr.damnardev.twitch.bot.database.entity.DbChannel;
import fr.damnardev.twitch.bot.domain.model.Channel;

import org.springframework.stereotype.Service;

@Service
public class ChannelMapper {

	public Channel toModel(DbChannel dbChannel) {
		return Channel.builder()
				.id(dbChannel.getId())
				.name(dbChannel.getName())
				.online(dbChannel.isOnline())
				.enabled(dbChannel.isEnabled())
				.build();
	}

}
