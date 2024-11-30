package fr.damnardev.twitch.bot.secondary.adapter;

import fr.damnardev.twitch.bot.database.repository.DbSuggestGameRepository;
import fr.damnardev.twitch.bot.model.Channel;
import fr.damnardev.twitch.bot.model.SuggestGame;
import fr.damnardev.twitch.bot.port.secondary.SuggestGameRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultSuggestGameRepository implements SuggestGameRepository {

	private static final String GOOGLE_FORM_URL = "https://docs.google.com/forms/d/e/%s/formResponse";

	private final DbSuggestGameRepository dbSuggestGameRepository;

	private final RestTemplate restTemplate;

	@Override
	public boolean suggest(Channel channel, SuggestGame suggestGame) {
		log.info("Suggesting game {} for channel: {}", suggestGame, channel);
		var optionalSuggestGame = this.dbSuggestGameRepository.findByChannelName(channel.name());
		if (optionalSuggestGame.isEmpty()) {
			log.error("No suggest game found for channel: {}", channel);
			return false;
		}
		var dbSuggestGame = optionalSuggestGame.get();
		var url = String.format(GOOGLE_FORM_URL, dbSuggestGame.getFormId());

		var headers = new HttpHeaders();
		headers.set("User-Agent", "Mozilla/5.0");
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		var formData = new LinkedMultiValueMap<>();
		formData.add("entry." + dbSuggestGame.getViewerFieldId(), suggestGame.viewer());
		formData.add("entry." + dbSuggestGame.getGameFieldId(), suggestGame.game());
		formData.add("submissionTimestamp", System.currentTimeMillis() + "");

		var entity = new HttpEntity<>(formData, headers);
		var response = this.restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
		if (response.getStatusCode().is2xxSuccessful()) {
			return true;
		}
		log.info("Suggesting game failed for channel: {}%n{}%n{}", channel, suggestGame, response);
		return false;
	}

}
