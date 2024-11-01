package fr.damnardev.twitch.bot.domain.port.primary;

public interface AuthenticationService {

	boolean isInitialized();

	void tryRenew();

}
