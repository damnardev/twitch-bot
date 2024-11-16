package fr.damnardev.twitch.bot.domain.service.channel;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.exception.BusinessException;
import fr.damnardev.twitch.bot.domain.model.Channel;
import fr.damnardev.twitch.bot.domain.model.event.ChannelCreatedEvent;
import fr.damnardev.twitch.bot.domain.model.form.CreateChannelForm;
import fr.damnardev.twitch.bot.domain.port.primary.TryService;
import fr.damnardev.twitch.bot.domain.port.primary.channel.CreateChannelService;
import fr.damnardev.twitch.bot.domain.port.secondary.EventPublisher;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.FindChannelRepository;
import fr.damnardev.twitch.bot.domain.port.secondary.channel.SaveChannelRepository;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultCreateChannelService implements CreateChannelService {

	private final TryService tryService;

	private final FindChannelRepository findChannelRepository;

	private final SaveChannelRepository saveChannelRepository;

	private final EventPublisher eventPublisher;

	@Override
	public void process(CreateChannelForm form) {
		this.tryService.doTry(this::doInternal, form);
	}

	private void doInternal(CreateChannelForm form) {
		var name = form.name();
		var optionalChannel = this.findChannelRepository.findByName(name);
		if (optionalChannel.isPresent()) {
			throw new BusinessException("Channel already exists");
		}
		var channel = Channel.builder().name(name).build();
		channel = this.saveChannelRepository.save(channel);
		var event = ChannelCreatedEvent.builder().channel(channel).build();
		this.eventPublisher.publish(event);
	}

}
