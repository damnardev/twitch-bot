package org.damnardev.twitch.bot.domain.port.secondary;

public interface IAuthenticationRepository {

    boolean isInitialized();

    boolean validateToken();

}
