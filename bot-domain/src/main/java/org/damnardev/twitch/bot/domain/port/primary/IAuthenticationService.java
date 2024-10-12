package org.damnardev.twitch.bot.domain.port.primary;

public interface IAuthenticationService {

    boolean isInitialized();

    void reconnect();

    void validateToken();

}
