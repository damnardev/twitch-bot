package fr.damnardev.twitch.bot.database.repository;

import java.util.List;
import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbChannel;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbChannelRepository extends JpaRepository<DbChannel, Long> {

	@Query("SELECT c FROM DbChannel c WHERE c.enabled = true")
	List<DbChannel> findAllEnabled();

	@Query("SELECT c FROM DbChannel c WHERE c.name = :name")
	Optional<DbChannel> findByName(@Param("name") String name);

}
