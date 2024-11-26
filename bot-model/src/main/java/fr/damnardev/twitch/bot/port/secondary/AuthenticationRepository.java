package fr.damnardev.twitch.bot.port.secondary;

public interface AuthenticationRepository {

	boolean isInitialized();

	boolean isValid();

	boolean renew();

}
