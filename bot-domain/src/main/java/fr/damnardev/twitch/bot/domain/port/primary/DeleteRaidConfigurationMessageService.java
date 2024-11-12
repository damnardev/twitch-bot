package fr.damnardev.twitch.bot.domain.port.primary;

import fr.damnardev.twitch.bot.domain.model.form.DeleteRaidConfigurationMessageForm;

public interface DeleteRaidConfigurationMessageService {

	void delete(DeleteRaidConfigurationMessageForm form);

}
