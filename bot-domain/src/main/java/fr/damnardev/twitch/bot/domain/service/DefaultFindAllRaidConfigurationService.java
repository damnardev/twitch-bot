package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationFindAllEvent;
import fr.damnardev.twitch.bot.domain.port.primary.FindAllRaidConfigurationService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultFindAllRaidConfigurationService implements FindAllRaidConfigurationService {

	private final DefaultTryService tryService;

	private final FindRaidConfigurationRepository findRaidConfigurationRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void findAll() {
		this.tryService.doTry(this::doInternal);
	}

	private void doInternal() {
		var configurations = this.findRaidConfigurationRepository.findAll();
		var event = RaidConfigurationFindAllEvent.builder().configurations(configurations).build();
		this.eventPublisher.publish(event);
	}

}
