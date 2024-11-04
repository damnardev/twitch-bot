package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.Shoutout;

public interface ShoutoutRepository {

	void sendShoutout(Shoutout shoutout);

}
