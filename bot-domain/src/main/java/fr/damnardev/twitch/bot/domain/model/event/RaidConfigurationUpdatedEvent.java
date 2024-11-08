package fr.damnardev.twitch.bot.domain.model.event;

import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class RaidConfigurationUpdatedEvent extends Event {

	RaidConfiguration raidConfiguration;

	@Builder
	public RaidConfigurationUpdatedEvent(RaidConfiguration raidConfiguration, String error) {
		super(error);
		this.raidConfiguration = raidConfiguration;
	}

}
