package fr.damnardev.twitch.bot.secondary.configuration;

import com.github.philippheuer.credentialmanager.CredentialManager;
import com.github.philippheuer.credentialmanager.CredentialManagerBuilder;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.TwitchClientBuilder;
import com.github.twitch4j.TwitchClientHelper;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import com.github.twitch4j.chat.TwitchChat;
import com.github.twitch4j.helix.TwitchHelix;
import fr.damnardev.twitch.bot.secondary.PropertiesConfiguration;
import fr.damnardev.twitch.bot.secondary.property.TwitchOAuthProperties;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;

@AutoConfigureAfter(PropertiesConfiguration.class)
@Configuration
public class Twitch4JConfiguration {

	@Bean
	public OAuth2Credential credential() {
		return new OAuth2Credential("twitch", "");
	}

	@Bean
	public CredentialManager credentialManager() {
		return CredentialManagerBuilder.builder().build();
	}

	@Bean("twitchClient")
	public TwitchClient twitchClient(CredentialManager credentialManager, OAuth2Credential credential, TwitchOAuthProperties twitchOAuthProperties) {
		var clientBuilder = TwitchClientBuilder.builder();
		return clientBuilder.withCredentialManager(credentialManager).withClientId(twitchOAuthProperties.getClientId()).withClientSecret(null).withDefaultAuthToken(credential).withEnableHelix(true).withChatAccount(credential).withEnableChat(true).build();
	}

	@Bean
	@DependsOn("twitchClient")
	public TwitchIdentityProvider identityProvider(CredentialManager credentialManager) {
		return credentialManager.getIdentityProviderByName("twitch").filter(TwitchIdentityProvider.class::isInstance).map(TwitchIdentityProvider.class::cast).orElseThrow();
	}

	@Bean
	@DependsOn("twitchClient")
	public TwitchChat twitchChat(TwitchClient twitchClient) {
		return twitchClient.getChat();
	}

	@Bean
	@DependsOn("twitchClient")
	public TwitchClientHelper twitchClientHelper(TwitchClient twitchClient) {
		return twitchClient.getClientHelper();
	}

	@Bean
	@DependsOn("twitchClient")
	public TwitchHelix twitchHelix(TwitchClient twitchClient) {
		return twitchClient.getHelix();
	}

}
