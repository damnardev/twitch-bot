package fr.damnardev.twitch.bot.domain.port.primary.channel;

import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.EventService;

public interface DeleteChannelService extends EventService<DeleteChannelForm> {
}
