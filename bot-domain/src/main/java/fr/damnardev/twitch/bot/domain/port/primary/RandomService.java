package fr.damnardev.twitch.bot.domain.port.primary;

import java.util.List;

public interface RandomService {

	<T> T getRandom(List<T> values);

}
