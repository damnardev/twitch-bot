package fr.damnardev.twitch.bot.port.primary;

public interface AuthenticationService {

	boolean isInitialized();

	void tryRenew();

}
