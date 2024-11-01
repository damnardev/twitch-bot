package fr.damnardev.twitch.bot.domain.port.primary;

import fr.damnardev.twitch.bot.domain.model.form.DeleteChannelForm;

public interface DeleteChannelService {

	void delete(DeleteChannelForm form);

}
