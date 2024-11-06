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
public class RaidConfigurationFindEvent extends Event {

	List<RaidConfiguration> configurations;

	@Builder
	public RaidConfigurationFindEvent(List<RaidConfiguration> configurations, String error) {
		super(error);
		this.configurations = configurations;
	}

}
