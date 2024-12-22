package fr.damnardev.twitch.bot.model.event;

import java.util.List;

import fr.damnardev.twitch.bot.model.RaidConfiguration;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class RaidConfigurationFetchedAllEvent extends Event<List<RaidConfiguration>> {

	@Builder
	public RaidConfigurationFetchedAllEvent(List<RaidConfiguration> raidConfigurations) {
		super(raidConfigurations);
	}

	@Override
	public boolean hasError() {
		return false;
	}

}
