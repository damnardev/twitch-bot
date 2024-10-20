package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface IShoutoutRepository {

    void send(ChannelInfo from, ChannelInfo to);

}
