package fr.damnardev.twitch.bot.domain.service.raid;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.event.RaidConfigurationUpdatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.UpdateRaidConfigurationForm;
import fr.damnardev.twitch.bot.domain.port.primary.raid.UpdateRaidConfigurationService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.raid.FindRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.raid.UpdateRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.service.DefaultTryService;

@DomainService
public class DefaultUpdateRaidConfigurationMessageService extends AbstractRaidConfigurationMessageService implements UpdateRaidConfigurationService {

	private final DefaultTryService tryService;

	private final UpdateRaidConfigurationRepository updateRaidConfigurationRepository;

	public DefaultUpdateRaidConfigurationMessageService(FindChannelRepository findChannelRepository, FindRaidConfigurationRepository findRaidConfigurationRepository, EventPublisher eventPublisher, DefaultTryService tryService, UpdateRaidConfigurationRepository updateRaidConfigurationRepository) {
		super(findChannelRepository, findRaidConfigurationRepository, eventPublisher);
		this.tryService = tryService;
		this.updateRaidConfigurationRepository = updateRaidConfigurationRepository;
	}

	@Override
	public void process(UpdateRaidConfigurationForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(UpdateRaidConfigurationForm form) {
		var raidConfiguration = getRaidConfiguration(form.channelName());
		if (raidConfiguration == null) {
			return;
		}
		if (form.wizebotShoutoutEnabled() != null) {
			raidConfiguration = raidConfiguration.toBuilder().wizebotShoutoutEnabled(form.wizebotShoutoutEnabled()).build();
		}
		if (form.twitchShoutoutEnabled() != null) {
			raidConfiguration = raidConfiguration.toBuilder().twitchShoutoutEnabled(form.twitchShoutoutEnabled()).build();
		}
		if (form.raidMessageEnabled() != null) {
			raidConfiguration = raidConfiguration.toBuilder().raidMessageEnabled(form.raidMessageEnabled()).build();
		}
		this.updateRaidConfigurationRepository.update(raidConfiguration);
		var event = RaidConfigurationUpdatedEvent.builder().raidConfiguration(raidConfiguration).build();
		this.eventPublisher.publish(event);
	}

}
