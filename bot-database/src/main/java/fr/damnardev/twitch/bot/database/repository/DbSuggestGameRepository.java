package fr.damnardev.twitch.bot.database.repository;

import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbSuggestGame;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DbSuggestGameRepository extends JpaRepository<DbSuggestGame, Long> {

	@Query("SELECT sg FROM DbSuggestGame sg JOIN FETCH sg.channel WHERE upper(sg.channel.name) = upper(:name)")
	Optional<DbSuggestGame> findByChannelName(@Param("name") String name);

}
