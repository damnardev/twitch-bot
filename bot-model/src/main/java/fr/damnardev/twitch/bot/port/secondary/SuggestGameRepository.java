package fr.damnardev.twitch.bot.port.secondary;

import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.SuggestGame;

public interface SuggestGameRepository {

	boolean suggest(Channel channel, SuggestGame suggestGame);

}
