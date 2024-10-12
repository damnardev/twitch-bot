package org.damnardev.twitch.bot.database.repository;

import org.damnardev.twitch.bot.database.entity.ChannelCommand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DbChannelCommandRepository extends JpaRepository<ChannelCommand, Long> {

    @Query("SELECT c FROM ChannelCommand c WHERE LOWER(c.channel.name) = LOWER(:channel) AND LOWER(c.name) = LOWER(:name)")
    Optional<ChannelCommand> find(@Param("channel") String channel, @Param("name") String name);

}
