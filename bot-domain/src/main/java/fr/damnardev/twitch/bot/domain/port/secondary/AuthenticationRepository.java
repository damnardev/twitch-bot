package fr.damnardev.twitch.bot.domain.port.secondary;

public interface AuthenticationRepository {

	boolean isInitialized();

	boolean isValid();

	boolean renew();

}
