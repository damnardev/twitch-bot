package fr.damnardev.twitch.bot.secondary.adapter;

import java.time.LocalDate;
import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbSaint;
import fr.damnardev.twitch.bot.database.repository.DbSaintRepository;
import fr.damnardev.twitch.bot.exception.FatalException;
import fr.damnardev.twitch.bot.port.secondary.SaintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultSaintRepository implements SaintRepository {

	private final DbSaintRepository dbSaintRepository;

	private final RestTemplate restTemplate;

	@Value("${twitch.saint.url}")
	private String value;

	@Override
	public Optional<String> find() {
		try {
			var date = LocalDate.now();
			var optionalDbSaint = this.dbSaintRepository.findById(date);
			return optionalDbSaint.map(DbSaint::getMessage).or(() -> fetchAndSave(date));
		}
		catch (Exception ex) {
			throw new FatalException(ex);
		}
	}

	@SuppressWarnings("java:S112")
	private Optional<String> fetchAndSave(LocalDate date) {
		var headers = new HttpHeaders();
		headers.set("User-Agent", "Mozilla/5.0");
		var entity = new HttpEntity<>(headers);
		var response = restTemplate.exchange(value, HttpMethod.GET, entity, String.class).getBody();
		var dbSaint = DbSaint.builder().id(date).message(response).build();
		this.dbSaintRepository.save(dbSaint);
		return Optional.ofNullable(response);
	}

}
