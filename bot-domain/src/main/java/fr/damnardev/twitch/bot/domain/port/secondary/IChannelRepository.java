package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.ChannelInfo;

public interface IChannelRepository {

    void computeStatus();

    ChannelInfo find(String channelName);

    void joinAllChannel();

    void reconnect();

    void setOnline(ChannelInfo channel);

    void setOffline(ChannelInfo channel);

    ChannelInfo addChannel(ChannelInfo channel);

}
