package fr.damnardev.twitch.bot.secondary;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.philippheuer.credentialmanager.domain.OAuth2Credential;
import fr.damnardev.twitch.bot.domain.exception.FatalException;
import lombok.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

@RequiredArgsConstructor
public class TokenHandler {

    private final ObjectMapper objectMapper;

    public void write(OAuth2Credential credential) {
        var lightToken = LightToken.builder().refreshToken(credential.getRefreshToken()).build();
        try (var file = new FileOutputStream("token.json")) {
            objectMapper.writeValue(file, lightToken);
        } catch (IOException e) {
            throw new FatalException(e);
        }
    }

    public OAuth2Credential read() {
        OAuth2Credential twitch = new OAuth2Credential("twitch", "");
        twitch.setExpiresIn(0);
        try (var file = new FileInputStream("token.json")) {
            var lightToken = objectMapper.readValue(file, LightToken.class);
            twitch.setRefreshToken(lightToken.getRefreshToken());
            return twitch;
        } catch (IOException e) {
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
