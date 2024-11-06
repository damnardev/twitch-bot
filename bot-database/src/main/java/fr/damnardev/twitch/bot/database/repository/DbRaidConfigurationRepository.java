package fr.damnardev.twitch.bot.database.repository;

import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbRaidConfiguration;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbRaidConfigurationRepository extends JpaRepository<DbRaidConfiguration, Long> {

	@Query("SELECT rc FROM DbRaidConfiguration rc JOIN FETCH rc.channel WHERE upper(rc.channel.name) = upper(:name)")
	Optional<DbRaidConfiguration> findByChannelName(@Param("name") String name);

}
