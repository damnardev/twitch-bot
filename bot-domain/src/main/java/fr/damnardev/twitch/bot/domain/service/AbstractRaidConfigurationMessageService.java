package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import fr.damnardev.twitch.bot.domain.model.event.ChannelRaidEvent;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class AbstractRaidConfigurationMessageService {

	protected final FindChannelRepository findChannelRepository;

	protected final FindRaidConfigurationRepository findRaidConfigurationRepository;

	protected final EventPublisher eventPublisher;

	protected RaidConfiguration getRaidConfiguration(String name) {
		var optionalChannel = this.findChannelRepository.findByName(name);
		if (optionalChannel.isEmpty()) {
			var event = ChannelRaidEvent.builder().error("Channel not found").build();
			this.eventPublisher.publish(event);
			return null;
		}
		var optionalRaidConfiguration = this.findRaidConfigurationRepository.findByChannelName(name);
		if (optionalRaidConfiguration.isEmpty()) {
			var event = ChannelRaidEvent.builder().error("Channel Raid Configuration not found").build();
			this.eventPublisher.publish(event);
			return null;
		}
		return optionalRaidConfiguration.get();
	}

}
