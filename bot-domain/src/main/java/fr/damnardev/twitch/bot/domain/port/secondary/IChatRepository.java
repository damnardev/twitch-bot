package fr.damnardev.twitch.bot.domain.port.secondary;

public interface IChatRepository {

    void joinAllChannel();

    void reconnect();

}
