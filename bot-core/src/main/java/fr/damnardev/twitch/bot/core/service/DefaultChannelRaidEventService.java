package fr.damnardev.twitch.bot.core.service;

import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.exception.BusinessException;
import fr.damnardev.twitch.bot.model.Message;
import fr.damnardev.twitch.bot.model.Shoutout;
import fr.damnardev.twitch.bot.model.form.ChannelRaidEventForm;
import fr.damnardev.twitch.bot.port.primary.ChannelRaidEventService;
import fr.damnardev.twitch.bot.port.primary.RandomService;
import fr.damnardev.twitch.bot.port.secondary.MessageRepository;
import fr.damnardev.twitch.bot.port.secondary.ShoutoutRepository;
import fr.damnardev.twitch.bot.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.port.secondary.raid.FindRaidConfigurationRepository;
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

	@Override
	public void process(ChannelRaidEventForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(ChannelRaidEventForm form) {
		var optionalChannel = this.findChannelRepository.findByName(form.channelName());
		if (optionalChannel.isEmpty()) {
			throw new BusinessException("Channel not found");
		}
		var channel = optionalChannel.get();

		if (channel.isOffline()) {
			return;
		}

		var optionalRaidConfiguration = this.findRaidConfigurationRepository.findByChannel(channel);
		if (optionalRaidConfiguration.isEmpty()) {
			throw new BusinessException("Channel Raid Configuration not found");
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
