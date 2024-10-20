package fr.damnardev.twitch.bot.domain.port.primary;

public interface IEventService<M> {

    void process(M event);

}
