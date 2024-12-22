package fr.damnardev.twitch.bot.port.secondary;

import java.util.Optional;

public interface SaintRepository {

	Optional<String> find();

}
