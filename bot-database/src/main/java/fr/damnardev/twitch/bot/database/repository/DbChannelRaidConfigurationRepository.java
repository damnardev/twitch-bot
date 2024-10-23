package fr.damnardev.twitch.bot.database.repository;

import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.ChannelRaid;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbChannelRaidConfigurationRepository extends JpaRepository<ChannelRaid, Long> {

	@Query("SELECT c FROM ChannelRaid c WHERE c.id = :id")
	Optional<ChannelRaid> find(@Param("id") long id);

}
