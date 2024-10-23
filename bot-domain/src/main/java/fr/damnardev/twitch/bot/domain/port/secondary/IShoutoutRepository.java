package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.User;

public interface IShoutoutRepository {

    void send(User channel, User raider);

}
