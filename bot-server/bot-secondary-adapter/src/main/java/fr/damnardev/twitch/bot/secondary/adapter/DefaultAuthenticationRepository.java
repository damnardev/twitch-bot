package fr.damnardev.twitch.bot.secondary.adapter;

import com.github.philippheuer.credentialmanager.domain.DeviceAuthorization;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import fr.damnardev.twitch.bot.database.entity.DbCredential;
import fr.damnardev.twitch.bot.database.repository.DbCredentialRepository;
import fr.damnardev.twitch.bot.exception.FatalException;
import fr.damnardev.twitch.bot.port.secondary.AuthenticationRepository;
import fr.damnardev.twitch.bot.secondary.property.TwitchOAuthProperties;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultAuthenticationRepository implements AuthenticationRepository {

	private final TwitchOAuthProperties properties;

	private final TwitchIdentityProvider provider;

	private final OAuth2Credential credential;

	private final DbCredentialRepository dbCredentialRepository;

	private boolean initialized = false;

	@PostConstruct
	public void init() {
		this.dbCredentialRepository.findLast().ifPresent((dbCredential) -> this.credential.setRefreshToken(dbCredential.getRefreshToken()));
	}

	@Override
	@Transactional
	public boolean renew() {
		log.info("Renewing token");
		if (this.provider.renew(this.credential) || generateDeviceToken()) {
			log.info("Token renewed");
			this.credential.updateCredential(this.provider.getAdditionalCredentialInformation(this.credential).orElseThrow());
			this.credential.setExpiresIn(this.credential.getExpiresIn() - 1800);
			var refreshToken = DbCredential.builder().refreshToken(this.credential.getRefreshToken()).build();
			this.dbCredentialRepository.save(refreshToken);
			this.initialized = true;
			return true;
		}
		log.error("Failed to renew token");
		return false;
	}

	private boolean generateDeviceToken() {
		log.info("Generating device token");
		var flowRequest = this.provider.createDeviceFlowRequest(this.properties.getScopes());
		displayBrowserLink(flowRequest);
		return attemptTokenGeneration(flowRequest);
	}

	private void displayBrowserLink(DeviceAuthorization flowRequest) {
		log.info("Please go to {}", flowRequest.getVerificationUri());
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
