package fr.damnardev.twitch.bot.domain.exception;

public class FatalException extends RuntimeException {

	public FatalException(Exception ex) {
		super(ex);
	}

}
