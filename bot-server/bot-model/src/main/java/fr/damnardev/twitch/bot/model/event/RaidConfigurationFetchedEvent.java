package fr.damnardev.twitch.bot.model.event;

import fr.damnardev.twitch.bot.model.RaidConfiguration;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class RaidConfigurationFetchedEvent extends Event<RaidConfiguration> {

	@Builder
	public RaidConfigurationFetchedEvent(RaidConfiguration raidConfiguration) {
		super(raidConfiguration);
	}

	@Override
	public boolean hasError() {
		return false;
	}

}
