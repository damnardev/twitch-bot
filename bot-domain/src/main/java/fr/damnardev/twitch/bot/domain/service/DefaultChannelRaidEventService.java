package fr.damnardev.twitch.bot.domain.service;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.model.Message;
import fr.damnardev.twitch.bot.domain.model.Shoutout;
import fr.damnardev.twitch.bot.domain.model.event.ChannelRaidEvent;
import fr.damnardev.twitch.bot.domain.model.form.ChannelRaidEventForm;
import fr.damnardev.twitch.bot.domain.port.primary.ChannelRaidEventService;
import fr.damnardev.twitch.bot.domain.port.primary.RandomService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.FindRaidConfigurationRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.MessageRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.ShoutoutRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultChannelRaidEventService implements ChannelRaidEventService {

	private final DefaultTryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final FindRaidConfigurationRepository findRaidConfigurationRepository;

	private final MessageRepository messageRepository;

	private final ShoutoutRepository shoutoutRepository;

	private final RandomService randomService;

	private final EventPublisher eventPublisher;

	@Override
	public void process(ChannelRaidEventForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(ChannelRaidEventForm form) {
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
		var channelRaidConfiguration = optionalRaidConfiguration.get();
		if (channelRaidConfiguration.raidMessageEnabled()) {
			var str = this.randomService.getRandom(channelRaidConfiguration.messages());
			var message = Message.builder().channelId(channel.id()).channelName(channel.name()).content(str.formatted(form.raiderName())).build();
			this.messageRepository.sendMessage(message);
		}
		if (channelRaidConfiguration.wizebotShoutoutEnabled()) {
			var message = Message.builder().channelId(channel.id()).channelName(channel.name()).content("!so %s".formatted(form.raiderName())).build();
			this.messageRepository.sendMessage(message);
		}
		if (channelRaidConfiguration.twitchShoutoutEnabled()) {
			var shoutout = Shoutout.builder().channelId(channel.id()).channelName(channel.name()).raiderId(form.raiderId()).raiderName(form.raiderName()).build();
			this.shoutoutRepository.sendShoutout(shoutout);
		}
	}

}
