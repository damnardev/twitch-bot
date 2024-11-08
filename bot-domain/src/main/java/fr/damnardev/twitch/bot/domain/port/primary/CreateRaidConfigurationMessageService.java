package fr.damnardev.twitch.bot.domain.port.primary;

import fr.damnardev.twitch.bot.domain.model.form.CreateRaidConfigurationMessageForm;

public interface CreateRaidConfigurationMessageService {

	void save(CreateRaidConfigurationMessageForm form);

}
