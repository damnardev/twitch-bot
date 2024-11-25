package fr.damnardev.twitch.bot.port.secondary;

import fr.damnardev.twitch.bot.model.Shoutout;

public interface ShoutoutRepository {

	void sendShoutout(Shoutout shoutout);

}
