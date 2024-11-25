package fr.damnardev.twitch.bot.port.primary;

import java.util.List;

public interface RandomService {

	<T> T getRandom(List<T> values);

}
