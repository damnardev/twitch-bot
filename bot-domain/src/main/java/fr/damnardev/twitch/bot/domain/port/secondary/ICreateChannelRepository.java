package fr.damnardev.twitch.bot.domain.port.secondary;

import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;

public interface ICreateChannelRepository {

	Channel save(CreateChannelForm form);

}
