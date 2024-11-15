package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationFindEvent;
import fr.damnardev.twitch.bot.domain.port.primary.FindRaidConfigurationService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;

@DomainService
public class DefaultFindRaidConfigurationService extends AbstractRaidConfigurationMessageService implements FindRaidConfigurationService {

	private final DefaultTryService tryService;

	public DefaultFindRaidConfigurationService(FindChannelRepository findChannelRepository, FindRaidConfigurationRepository findRaidConfigurationRepository, EventPublisher eventPublisher, DefaultTryService tryService) {
		super(findChannelRepository, findRaidConfigurationRepository, eventPublisher);
		this.tryService = tryService;
	}

	@Override
	public void findByChannelName(String name) {
		this.tryService.doTry(this::doInternal, name);
	}

	private void doInternal(String name) {
		var raidConfiguration = getRaidConfiguration(name);
		if (raidConfiguration == null) {
			return;
		}
		var event = RaidConfigurationFindEvent.builder().configuration(raidConfiguration).build();
		this.eventPublisher.publish(event);
	}

}
