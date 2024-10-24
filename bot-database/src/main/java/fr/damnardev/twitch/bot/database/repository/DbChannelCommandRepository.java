package fr.damnardev.twitch.bot.database.repository;

import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.ChannelCommand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbChannelCommandRepository extends JpaRepository<ChannelCommand, Long> {

	@Query("SELECT c FROM ChannelCommand c WHERE LOWER(c.channel.name) = LOWER(:channel) AND LOWER(c.name) = LOWER(:name)")
	Optional<ChannelCommand> find(@Param("channel") String channel, @Param("name") String name);

}
