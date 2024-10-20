package fr.damnardev.twitch.bot.secondary.property;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.concurrent.TimeUnit;

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
