package fr.damnardev.twitch.bot.secondary.property;

import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class TwitchOAuthProperties {

	private String clientId;

	private List<Object> scopes;

	private int retry;

	private int timeout;

	private TimeUnit timeoutUnit;

}
