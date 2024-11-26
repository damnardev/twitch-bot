package fr.damnardev.twitch.bot.core.service.raid;

import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.core.service.DefaultTryService;
import fr.damnardev.twitch.bot.model.event.RaidConfigurationFetchedAllEvent;
import fr.damnardev.twitch.bot.port.primary.raid.FetchAllRaidConfigurationService;
import fr.damnardev.twitch.bot.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.port.secondary.raid.FindRaidConfigurationRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultFetchAllRaidConfigurationService implements FetchAllRaidConfigurationService {

	private final DefaultTryService tryService;

	private final FindRaidConfigurationRepository findRaidConfigurationRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void process() {
		this.tryService.doTry(this::raidConfigurations);
	}

	private void raidConfigurations() {
		var configurations = this.findRaidConfigurationRepository.findAll();
		var event = RaidConfigurationFetchedAllEvent.builder().raidConfigurations(configurations).build();
		this.eventPublisher.publish(event);
	}

}
