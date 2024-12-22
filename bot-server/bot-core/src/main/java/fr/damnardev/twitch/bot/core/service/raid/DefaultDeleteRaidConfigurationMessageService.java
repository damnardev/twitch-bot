package fr.damnardev.twitch.bot.core.service.raid;

import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.core.service.DefaultTryService;
import fr.damnardev.twitch.bot.model.event.RaidConfigurationUpdatedEvent;
import fr.damnardev.twitch.bot.model.form.DeleteRaidConfigurationMessageForm;
import fr.damnardev.twitch.bot.port.primary.raid.DeleteRaidConfigurationMessageService;
import fr.damnardev.twitch.bot.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.port.secondary.raid.FindRaidConfigurationRepository;
import fr.damnardev.twitch.bot.port.secondary.raid.UpdateRaidConfigurationRepository;

@DomainService
public class DefaultDeleteRaidConfigurationMessageService extends AbstractRaidConfigurationMessageService implements DeleteRaidConfigurationMessageService {

	private final DefaultTryService tryService;

	private final UpdateRaidConfigurationRepository updateRaidConfigurationRepository;

	public DefaultDeleteRaidConfigurationMessageService(FindChannelRepository findChannelRepository, FindRaidConfigurationRepository findRaidConfigurationRepository, EventPublisher eventPublisher, DefaultTryService tryService, UpdateRaidConfigurationRepository updateRaidConfigurationRepository) {
		super(findChannelRepository, findRaidConfigurationRepository, eventPublisher);
		this.tryService = tryService;
		this.updateRaidConfigurationRepository = updateRaidConfigurationRepository;
	}

	@Override
	public void process(DeleteRaidConfigurationMessageForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(DeleteRaidConfigurationMessageForm form) {
		var raidConfiguration = getRaidConfiguration(form.channelName());
		if (raidConfiguration == null) {
			return;
		}
		raidConfiguration.messages().remove(form.message());
		this.updateRaidConfigurationRepository.update(raidConfiguration);
		var event = RaidConfigurationUpdatedEvent.builder().raidConfiguration(raidConfiguration).build();
		this.eventPublisher.publish(event);
	}

}
