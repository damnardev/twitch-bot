package fr.damnardev.twitch.bot.domain.port.primary;

import java.util.List;

public interface IRandomService {

    <T> T getRandom(List<T> values);

}
