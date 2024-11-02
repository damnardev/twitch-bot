package fr.damnardev.twitch.bot.domain.port.primary;

public interface EventService<M> {

	void process(M event);

}
