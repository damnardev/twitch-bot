package fr.damnardev.twitch.bot.domain.service.raid;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationFetchedAllEvent;
import fr.damnardev.twitch.bot.domain.port.primary.raid.FetchAllRaidConfigurationService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.raid.FindRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.service.DefaultTryService;
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
