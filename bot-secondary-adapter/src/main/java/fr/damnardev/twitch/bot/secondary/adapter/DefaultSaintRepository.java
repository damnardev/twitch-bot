package fr.damnardev.twitch.bot.secondary.adapter;

import java.net.HttpURLConnection;
import java.net.URI;
import java.time.LocalDate;
import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbSaint;
import fr.damnardev.twitch.bot.database.repository.DbSaintRepository;
import fr.damnardev.twitch.bot.domain.exception.FatalException;
import fr.damnardev.twitch.bot.domain.port.secondary.SaintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class DefaultSaintRepository implements SaintRepository {

	private final DbSaintRepository dbSaintRepository;

	@Value("${twitch.saint.url}")
	private String value;

	@Override
	public Optional<String> find() {
		try {
			var date = LocalDate.now();
			var optionalDbSaint = dbSaintRepository.findById(date);
			if (optionalDbSaint.isPresent()) {
				return Optional.of(optionalDbSaint.get().getMessage());
			}
			return fetchAndSave(date);
		}
		catch (Exception ex) {
			throw new FatalException(ex);
		}
	}

	@SuppressWarnings("java:S11Z2")
	private Optional<String> fetchAndSave(LocalDate date) throws Exception {
		var url = URI.create(this.value).toURL();
		var connection = (HttpURLConnection) url.openConnection();
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		var bytes = connection.getInputStream().readAllBytes();
		var string = new String(bytes);
		var dbSaint = DbSaint.builder().id(date).message(string).build();
		dbSaintRepository.save(dbSaint);
		return Optional.of(string);
	}

}
