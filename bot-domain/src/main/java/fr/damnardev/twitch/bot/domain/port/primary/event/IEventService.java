package fr.damnardev.twitch.bot.domain.port.primary.event;

public interface IEventService<M> {

	void process(M event);

}
