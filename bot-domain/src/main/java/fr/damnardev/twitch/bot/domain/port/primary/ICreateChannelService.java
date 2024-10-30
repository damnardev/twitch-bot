package fr.damnardev.twitch.bot.domain.port.primary;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;

public interface ICreateChannelService {

	Channel save(CreateChannelForm form);

}
