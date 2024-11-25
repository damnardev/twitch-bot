package fr.damnardev.twitch.bot.domain.port.secondary;

import java.util.Optional;

public interface SaintRepository {

	Optional<String> find();

}
