package fr.damnardev.twitch.bot.core.service.raid;

import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.exception.BusinessException;
import fr.damnardev.twitch.bot.model.RaidConfiguration;
import fr.damnardev.twitch.bot.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.port.secondary.raid.FindRaidConfigurationRepository;
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
			throw new BusinessException("Channel not found");
		}
		var optionalRaidConfiguration = this.findRaidConfigurationRepository.findByChannel(optionalChannel.get());
		if (optionalRaidConfiguration.isEmpty()) {
			throw new BusinessException("Channel Raid Configuration not found");
		}
		return optionalRaidConfiguration.get();
	}

}
