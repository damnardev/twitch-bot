package fr.damnardev.twitch.bot.exception;

public class FatalException extends RuntimeException {

	public FatalException(Exception ex) {
		super(ex);
	}

	public FatalException(String message) {
		super(message);
	}

}
