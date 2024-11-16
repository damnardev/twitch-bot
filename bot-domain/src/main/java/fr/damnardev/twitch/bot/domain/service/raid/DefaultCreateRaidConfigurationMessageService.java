package fr.damnardev.twitch.bot.domain.service.raid;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.CreateRaidConfigurationMessageForm;
import fr.damnardev.twitch.bot.domain.port.primary.raid.CreateRaidConfigurationMessageService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.raid.FindRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.raid.UpdateRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.service.DefaultTryService;

@DomainService
public class DefaultCreateRaidConfigurationMessageService extends AbstractRaidConfigurationMessageService implements CreateRaidConfigurationMessageService {

	private final DefaultTryService tryService;

	private final UpdateRaidConfigurationRepository updateRaidConfigurationRepository;

	public DefaultCreateRaidConfigurationMessageService(FindChannelRepository findChannelRepository, FindRaidConfigurationRepository findRaidConfigurationRepository, EventPublisher eventPublisher, DefaultTryService tryService, UpdateRaidConfigurationRepository updateRaidConfigurationRepository) {
		super(findChannelRepository, findRaidConfigurationRepository, eventPublisher);
		this.tryService = tryService;
		this.updateRaidConfigurationRepository = updateRaidConfigurationRepository;
	}

	@Override
	public void process(CreateRaidConfigurationMessageForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(CreateRaidConfigurationMessageForm form) {
		var raidConfiguration = getRaidConfiguration(form.channelName());
		if (raidConfiguration == null) {
			return;
		}
		raidConfiguration.messages().add(form.message());
		this.updateRaidConfigurationRepository.update(raidConfiguration);
		var event = RaidConfigurationUpdatedEvent.builder().raidConfiguration(raidConfiguration).build();
		this.eventPublisher.publish(event);
	}

}
