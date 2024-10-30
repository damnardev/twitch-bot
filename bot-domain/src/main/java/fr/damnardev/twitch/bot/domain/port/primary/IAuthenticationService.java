package fr.damnardev.twitch.bot.domain.port.primary;

public interface IAuthenticationService {

	boolean isInitialized();

	void tryRenew();

}
