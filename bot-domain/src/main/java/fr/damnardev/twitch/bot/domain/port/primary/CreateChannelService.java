package fr.damnardev.twitch.bot.domain.port.primary;

import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;

public interface CreateChannelService {

	void save(CreateChannelForm form);

}
