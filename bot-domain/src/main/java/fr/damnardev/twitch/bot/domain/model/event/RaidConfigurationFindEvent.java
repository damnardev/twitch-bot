package fr.damnardev.twitch.bot.domain.model.event;

import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class RaidConfigurationFindEvent extends Event {

	RaidConfiguration configuration;

	@Builder
	public RaidConfigurationFindEvent(RaidConfiguration configuration, String error) {
		super(error);
		this.configuration = configuration;
	}

}
