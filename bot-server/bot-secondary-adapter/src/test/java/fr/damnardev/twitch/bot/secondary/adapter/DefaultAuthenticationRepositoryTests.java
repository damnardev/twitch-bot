package fr.damnardev.twitch.bot.secondary.adapter;

import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import com.github.philippheuer.credentialmanager.domain.DeviceAuthorization;
import com.github.philippheuer.credentialmanager.domain.DeviceTokenResponse;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import com.github.twitch4j.auth.providers.TwitchIdentityProvider;
import fr.damnardev.twitch.bot.database.entity.DbCredential;
import fr.damnardev.twitch.bot.database.repository.DbCredentialRepository;
import fr.damnardev.twitch.bot.secondary.property.TwitchOAuthProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.mock;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.times;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultAuthenticationRepositoryTests {

	public static final String REFRESH_TOKEN = "refresh_token";

	public static final String DEVICE_CODE = "device_code";

	public static final String SCOPE_01 = "scope_01";

	public static final String SCOPE_02 = "scope_02";

	public static final String HTTP_LOCALHOST = "http://localhost/";

	@InjectMocks
	private DefaultAuthenticationRepository authenticationRepository;

	@Mock
	private TwitchOAuthProperties properties;

	@Mock
	private TwitchIdentityProvider provider;

	@Mock
	private OAuth2Credential credential;

	@Mock
	private DbCredentialRepository dbCredentialRepository;

	@Test
	void init_shouldSetRefreshToken_whenDbCredentialIsPresent() {
		// Given
		var dbCredential = DbCredential.builder().refreshToken(REFRESH_TOKEN).build();
		var optional = Optional.of(dbCredential);

		given(this.dbCredentialRepository.findLast()).willReturn(optional);

		// When
		this.authenticationRepository.init();

		// Then
		then(this.dbCredentialRepository).should().findLast();
		then(this.credential).should().setRefreshToken(REFRESH_TOKEN);
		verifyNoMoreInteractions(this.properties, this.provider, this.credential, this.dbCredentialRepository);
	}

	@Test
	void init_shouldDoNothing_whenDbCredentialIsEmpty() {
		// Given
		given(this.dbCredentialRepository.findLast()).willReturn(Optional.empty());

		// When
		this.authenticationRepository.init();

		// Then
		then(this.dbCredentialRepository).should().findLast();
		verifyNoMoreInteractions(this.properties, this.provider, this.credential, this.dbCredentialRepository);
	}

	@ParameterizedTest
	@ValueSource(ints = { 0, 1, 2 })
	void renew_shouldReturnFalse_whenRetryIsExceeded(int retry) {
		// Given
		var scopes = Arrays.<Object>asList(SCOPE_01, SCOPE_02);
		var deviceAuthorization = mock(DeviceAuthorization.class);
		var deviceTokenResponse = mock(DeviceTokenResponse.class);

		given(this.provider.renew(this.credential)).willReturn(false);
		given(this.properties.getScopes()).willReturn(scopes);
		given(this.provider.createDeviceFlowRequest(scopes)).willReturn(deviceAuthorization);
		given(deviceAuthorization.getVerificationUri()).willReturn(HTTP_LOCALHOST);
		given(this.properties.getRetry()).willReturn(retry);

		if (retry > 0) {
			given(deviceAuthorization.getDeviceCode()).willReturn(DEVICE_CODE);
			given(this.provider.getDeviceAccessToken(DEVICE_CODE)).willReturn(deviceTokenResponse);
			given(deviceTokenResponse.getCredential()).willReturn(null);
			given(this.properties.getTimeoutUnit()).willReturn(TimeUnit.MICROSECONDS);
			given(this.properties.getTimeout()).willReturn(1);
		}

		// When
		var result = this.authenticationRepository.renew();

		// Then
		then(this.provider).should().renew(this.credential);
		then(this.properties).should().getScopes();
		then(this.provider).should().createDeviceFlowRequest(scopes);
		then(deviceAuthorization).should().getVerificationUri();
		then(this.properties).should().getRetry();
		var times = times(retry);
		then(deviceAuthorization).should(times).getDeviceCode();
		then(this.provider).should(times).getDeviceAccessToken(DEVICE_CODE);
		then(deviceTokenResponse).should(times).getCredential();
		then(this.properties).should(times).getTimeoutUnit();
		then(this.properties).should(times).getTimeout();
		verifyNoMoreInteractions(this.properties, this.provider, this.credential, this.dbCredentialRepository, deviceTokenResponse, deviceAuthorization);

		assertThat(result).isFalse();
	}

	@Test
	void renew_shouldReturnTrue_whenGenerateDevice() {
		// Given
		var scopes = Arrays.<Object>asList(SCOPE_01, SCOPE_02);
		var deviceAuthorization = mock(DeviceAuthorization.class);
		var deviceTokenResponse = mock(DeviceTokenResponse.class);
		var newCredential = mock(OAuth2Credential.class);
		var dbCredential = DbCredential.builder().refreshToken(REFRESH_TOKEN).build();

		given(this.provider.renew(this.credential)).willReturn(false);
		given(this.properties.getScopes()).willReturn(scopes);
		given(this.provider.createDeviceFlowRequest(scopes)).willReturn(deviceAuthorization);
		given(deviceAuthorization.getVerificationUri()).willReturn(HTTP_LOCALHOST);
		given(this.properties.getRetry()).willReturn(1);
		given(deviceAuthorization.getDeviceCode()).willReturn(DEVICE_CODE);
		given(this.provider.getDeviceAccessToken(DEVICE_CODE)).willReturn(deviceTokenResponse);
		given(deviceTokenResponse.getCredential()).willReturn(newCredential);
		given(this.provider.getAdditionalCredentialInformation(this.credential)).willReturn(Optional.of(this.credential));
		given(this.credential.getExpiresIn()).willReturn(3600);
		given(this.credential.getRefreshToken()).willReturn(REFRESH_TOKEN);
		given(this.dbCredentialRepository.save(dbCredential)).willReturn(dbCredential);

		// When
		var result = this.authenticationRepository.renew();

		// Then
		then(this.provider).should().renew(this.credential);
		then(this.properties).should().getScopes();
		then(this.provider).should().createDeviceFlowRequest(scopes);
		then(deviceAuthorization).should().getVerificationUri();
		then(this.properties).should().getRetry();
		then(deviceAuthorization).should().getDeviceCode();
		then(this.provider).should().getDeviceAccessToken(DEVICE_CODE);
		then(deviceTokenResponse).should().getCredential();
		then(this.credential).should().updateCredential(newCredential);
		then(this.credential).should().getExpiresIn();
		then(this.provider).should().getAdditionalCredentialInformation(this.credential);
		then(this.credential).should().updateCredential(this.credential);
		then(this.credential).should().setExpiresIn(1800);
		then(this.credential).should().getRefreshToken();
		then(this.dbCredentialRepository).should().save(dbCredential);
		verifyNoMoreInteractions(this.properties, this.provider, this.credential, this.dbCredentialRepository, deviceTokenResponse, deviceAuthorization);

		assertThat(result).isTrue();
	}

	@Test
	void renew_shouldReturnTrue_whenRenewIsCalled() {
		// Given
		var deviceAuthorization = mock(DeviceAuthorization.class);
		var deviceTokenResponse = mock(DeviceTokenResponse.class);
		var newCredential = mock(OAuth2Credential.class);
		var dbCredential = DbCredential.builder().refreshToken(REFRESH_TOKEN).build();

		given(this.provider.renew(this.credential)).willReturn(true);
		given(this.provider.getAdditionalCredentialInformation(this.credential)).willReturn(Optional.of(this.credential));
		given(this.credential.getExpiresIn()).willReturn(3600);
		given(this.credential.getRefreshToken()).willReturn(REFRESH_TOKEN);
		given(this.dbCredentialRepository.save(dbCredential)).willReturn(dbCredential);

		// When
		var result = this.authenticationRepository.renew();

		// Then
		then(this.provider).should().renew(this.credential);
		then(this.provider).should().getAdditionalCredentialInformation(this.credential);
		then(this.credential).should().updateCredential(this.credential);
		then(this.credential).should().getExpiresIn();
		then(this.credential).should().setExpiresIn(1800);
		then(this.credential).should().getRefreshToken();
		then(this.dbCredentialRepository).should().save(dbCredential);
		verifyNoMoreInteractions(this.properties, this.provider, this.credential, this.dbCredentialRepository, newCredential, deviceTokenResponse, deviceAuthorization);

		assertThat(result).isTrue();
	}

	@Test
	void isInitialized_shouldReturnFalse_whenCalled() {
		// When
		var result = this.authenticationRepository.isInitialized();

		// Then
		verifyNoMoreInteractions(this.properties, this.provider, this.credential, this.dbCredentialRepository);

		assertThat(result).isFalse();
	}

	@ParameterizedTest
	@ValueSource(booleans = { true, false })
	void isValid_shouldReturnExpectedValue_whenCalled(boolean expected) {
		// Given
		given(this.provider.isValid(this.credential)).willReturn(expected);

		// When
		var result = this.authenticationRepository.isValid();

		// Then
		then(this.provider).should().isValid(this.credential);
		verifyNoMoreInteractions(this.properties, this.provider, this.credential, this.dbCredentialRepository);

		assertThat(result).isEqualTo(expected);
	}

}
