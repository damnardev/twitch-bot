package fr.damnardev.twitch.bot.domain.port.primary.channel;

import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.EventService;

public interface CreateChannelService extends EventService<CreateChannelForm> {
}
