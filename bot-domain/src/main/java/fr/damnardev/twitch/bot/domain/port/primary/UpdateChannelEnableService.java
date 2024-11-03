package fr.damnardev.twitch.bot.domain.port.primary;

import fr.damnardev.twitch.bot.domain.model.form.UpdateChannelEnabledForm;

public interface UpdateChannelEnableService {

	void updateEnabled(UpdateChannelEnabledForm form);

}
