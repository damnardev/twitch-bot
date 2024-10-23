package fr.damnardev.twitch.bot.secondary.adapter;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;

import com.github.philippheuer.credentialmanager.domain.DeviceAuthorization;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import fr.damnardev.twitch.bot.domain.exception.FatalException;
import fr.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import fr.damnardev.twitch.bot.secondary.TokenHandler;
import fr.damnardev.twitch.bot.secondary.property.TwitchOAuthProperties;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthenticationRepository implements IAuthenticationRepository {

	private final TwitchOAuthProperties properties;

	private final TwitchIdentityProvider provider;

	private final OAuth2Credential credential;

	private final TokenHandler tokenHandler;

	private boolean initialized = false;

	@Override
	public boolean renew() {
		log.info("Renewing token");
		if (this.provider.renew(this.credential) || generateDeviceToken()) {
			log.info("Token renewed");
			this.credential.setExpiresIn(this.credential.getExpiresIn() - 1800);
			this.tokenHandler.write(this.credential);
			this.initialized = true;
			return true;
		}
		log.error("Failed to renew token");
		return false;
	}

	private boolean generateDeviceToken() {
		log.info("Generating device token");
		var flowRequest = this.provider.createDeviceFlowRequest(this.properties.getScopes());
		openBrowser(flowRequest);
		return attemptTokenGeneration(flowRequest);
	}

	private void openBrowser(DeviceAuthorization flowRequest) {
		log.info("Opening browser");
		try {
			Desktop.getDesktop().browse(URI.create(flowRequest.getVerificationUri()));
		}
		catch (IOException ex) {
			throw new FatalException(ex);
		}
	}

	private boolean attemptTokenGeneration(DeviceAuthorization flowRequest) {
		var attempt = 0;
		var retry = this.properties.getRetry();
		while (attempt < retry) {
			log.info("Attempt {} of {}", ++attempt, retry);
			if (tryGenerateToken(flowRequest)) {
				return true;
			}
			sleep();
		}
		return false;
	}

	private boolean tryGenerateToken(DeviceAuthorization flowRequest) {
		var deviceAccessToken = this.provider.getDeviceAccessToken(flowRequest.getDeviceCode());
		var updatedCredential = deviceAccessToken.getCredential();
		if (updatedCredential != null) {
			this.credential.updateCredential(updatedCredential);
			return true;
		}
		return false;
	}

	private void sleep() {
		try {
			this.properties.getTimeoutUnit().sleep(this.properties.getTimeout());
		}
		catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
			throw new FatalException(ex);
		}
	}

	@Override
	public boolean isInitialized() {
		return this.initialized;
	}

	@Override
	public boolean isValid() {
		log.info("Validating token");
		var valid = this.provider.isValid(this.credential);
		log.info("Token is {}", valid ? "valid" : "invalid");
		return valid;
	}

}
