package fr.damnardev.twitch.bot.secondary;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import fr.damnardev.twitch.bot.domain.exception.FatalException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TokenHandler {

	private final ObjectMapper objectMapper;

	public void write(OAuth2Credential credential) {
		var lightToken = LightToken.builder().refreshToken(credential.getRefreshToken()).build();
		try (var file = new FileOutputStream("token.json")) {
			this.objectMapper.writeValue(file, lightToken);
		}
		catch (IOException ex) {
			throw new FatalException(ex);
		}
	}

	public OAuth2Credential read() {
		OAuth2Credential twitch = new OAuth2Credential("twitch", "");
		twitch.setExpiresIn(0);
		try (var file = new FileInputStream("token.json")) {
			var lightToken = this.objectMapper.readValue(file, LightToken.class);
			twitch.setRefreshToken(lightToken.getRefreshToken());
			return twitch;
		}
		catch (IOException ex) {
			return twitch;
		}
	}

	@AllArgsConstructor
	@Builder
	@Data
	@NoArgsConstructor
	public static class LightToken {

		private String refreshToken;

	}

}
