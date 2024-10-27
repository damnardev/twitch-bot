package fr.damnardev.twitch.bot.database.repository;

import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbCredential;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface DbCredentialRepository extends JpaRepository<DbCredential, Long> {

	@Query("SELECT t FROM DbCredential t WHERE t.id = (SELECT MAX(t2.id) FROM DbCredential t2)")
	Optional<DbCredential> findLast();

}
