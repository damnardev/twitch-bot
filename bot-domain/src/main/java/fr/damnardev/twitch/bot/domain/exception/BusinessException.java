package fr.damnardev.twitch.bot.domain.exception;

public class BusinessException extends RuntimeException {

	public BusinessException(Exception ex) {
		super(ex);
	}

	public BusinessException(String message) {
		super(message);
	}

}
