package fr.damnardev.twitch.bot.database.repository;

import fr.damnardev.twitch.bot.database.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DbChannelRepository extends JpaRepository<Channel, Long> {

    @Query("SELECT c FROM Channel c WHERE LOWER(c.name) = LOWER(:name)")
    Optional<Channel> find(@Param("name") String name);

    @Query("SELECT c FROM Channel c WHERE c.botEnabled = true")
    List<Channel> findAllEnabled();

}
