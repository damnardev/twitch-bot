package fr.damnardev.twitch.bot.database.repository;

import java.time.LocalDate;

import fr.damnardev.twitch.bot.database.entity.DbSaint;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DbSaintRepository extends JpaRepository<DbSaint, LocalDate> {
}
