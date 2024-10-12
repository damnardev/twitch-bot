package org.damnardev.twitch.bot.secondary.adapter;

import com.github.philippheuer.credentialmanager.domain.DeviceAuthorization;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.damnardev.twitch.bot.domain.exception.FatalException;
import org.damnardev.twitch.bot.domain.port.secondary.IAuthenticationRepository;
import org.damnardev.twitch.bot.secondary.TokenHandler;
import org.damnardev.twitch.bot.secondary.property.TwitchOAuthProperties;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.io.IOException;
import java.net.URI;

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
    public boolean validateToken() {
        log.info("Validating token");
        if (provider.isValid(credential)) {
            return false;
        }
        log.info("Token is invalid");
        if (provider.renew(credential) || generateDeviceToken()) {
            log.info("Token is valid");
            credential.setExpiresIn(credential.getExpiresIn() - 1800); // prevent token expiration - 30 minutes
            tokenHandler.write(credential);
            initialized = true;
            return true;
        }
        log.error("cannot generate token");
        return false;
    }

    private boolean generateDeviceToken() {
        log.info("Generating device token");
        var flowRequest = provider.createDeviceFlowRequest(properties.getScopes());
        openBrowser(flowRequest);
        return attemptTokenGeneration(flowRequest);
    }

    private void openBrowser(DeviceAuthorization flowRequest) {
        log.info("Opening browser");
        try {
            Desktop.getDesktop()
                   .browse(URI.create(flowRequest.getVerificationUri()));
        } catch (IOException e) {
            throw new FatalException(e);
        }
    }

    private boolean attemptTokenGeneration(DeviceAuthorization flowRequest) {
        var attempt = 0;
        var retry = properties.getRetry();
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
        var deviceAccessToken = provider.getDeviceAccessToken(flowRequest.getDeviceCode());
        var updatedCredential = deviceAccessToken.getCredential();
        if (updatedCredential != null) {
            credential.updateCredential(updatedCredential);
            return true;
        }
        return false;
    }

    private void sleep() {
        try {
            properties.getTimeoutUnit()
                      .sleep(properties.getTimeout());
        } catch (InterruptedException ex) {
            Thread.currentThread()
                  .interrupt();
            throw new FatalException(ex);
        }
    }

    @Override
    public boolean isInitialized() {
        return initialized;
    }

}
