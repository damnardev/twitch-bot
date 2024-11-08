package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.ChannelRaidEvent;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.CreateRaidConfigurationMessageForm;
import fr.damnardev.twitch.bot.domain.port.primary.CreateRaidConfigurationMessageService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.UpdateRaidConfigurationRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultCreateRaidConfigurationMessageService implements CreateRaidConfigurationMessageService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final FindRaidConfigurationRepository findRaidConfigurationRepository;

	private final UpdateRaidConfigurationRepository updateRaidConfigurationRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void save(CreateRaidConfigurationMessageForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(CreateRaidConfigurationMessageForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.name());
		if (optionalChannel.isEmpty()) {
			var event = ChannelRaidEvent.builder().error("Channel not found").build();
			this.eventPublisher.publish(event);
			return;
		}
		var channel = optionalChannel.get();
		var optionalRaidConfiguration = this.findRaidConfigurationRepository.findByChannelName(channel.name());
		if (optionalRaidConfiguration.isEmpty()) {
			var event = ChannelRaidEvent.builder().error("Channel Raid Configuration not found").build();
			this.eventPublisher.publish(event);
			return;
		}
		var raidConfiguration = optionalRaidConfiguration.get();
		raidConfiguration.messages().add(form.message());
		this.updateRaidConfigurationRepository.update(raidConfiguration);
		var event = RaidConfigurationUpdatedEvent.builder().raidConfiguration(raidConfiguration).build();
		this.eventPublisher.publish(event);
	}

}
