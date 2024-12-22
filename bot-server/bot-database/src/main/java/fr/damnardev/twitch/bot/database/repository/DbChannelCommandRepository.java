package fr.damnardev.twitch.bot.database.repository;

import java.util.List;
import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbChannelCommand;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbChannelCommandRepository extends JpaRepository<DbChannelCommand, Long> {

	@Query("SELECT cc FROM DbChannelCommand cc JOIN FETCH cc.channel WHERE upper(cc.channel.name) = upper(:channelName)")
	List<DbChannelCommand> findByChannelName(@Param("channelName") String channelName);

	@Query("SELECT cc FROM DbChannelCommand cc JOIN FETCH cc.channel WHERE upper(cc.channel.name) = upper(:channelName) AND upper(cc.name) = upper(:name)")
	Optional<DbChannelCommand> findByChannelNameAndName(@Param("channelName") String channelName, @Param("name") String name);

}
