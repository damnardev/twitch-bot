package fr.damnardev.twitch.bot.domain.model.event;

import java.util.List;

import fr.damnardev.twitch.bot.domain.model.RaidConfiguration;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Value
public class RaidConfigurationFindAllEvent extends Event {

	List<RaidConfiguration> configurations;

	@Builder
	public RaidConfigurationFindAllEvent(List<RaidConfiguration> configurations, String error) {
		super(error);
		this.configurations = configurations;
	}

}
