package fr.damnardev.twitch.bot.port.secondary;

import java.util.Optional;

import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.SuggestGame;

public interface SuggestGameRepository {

	Optional<String> suggest(Channel channel, SuggestGame suggestGame);

}
