package org.damnardev.twitch.bot.database.repository;

import org.damnardev.twitch.bot.database.entity.Bot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbBotRepository extends JpaRepository<Bot, Long> {

}
