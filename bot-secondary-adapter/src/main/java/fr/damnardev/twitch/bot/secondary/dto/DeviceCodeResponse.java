package fr.damnardev.twitch.bot.secondary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@NoArgsConstructor
public class DeviceCodeResponse {

    @JsonProperty("device_code")
    private String deviceCode;

    @JsonProperty("verification_uri")
    private String verificationUri;

}
