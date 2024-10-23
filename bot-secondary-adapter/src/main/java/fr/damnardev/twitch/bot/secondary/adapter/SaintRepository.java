package fr.damnardev.twitch.bot.secondary.adapter;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;

import fr.damnardev.twitch.bot.domain.exception.FatalException;
import fr.damnardev.twitch.bot.domain.model.Saint;
import fr.damnardev.twitch.bot.domain.port.secondary.ISaintRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class SaintRepository implements ISaintRepository {

	@Value("${twitch.saint.url}")
	private String value;

	private void fetchAndSave(Path path) throws IOException {
		try {
			var url = URI.create(this.value).toURL();
			var connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("User-Agent", "Mozilla/5.0");
			var bytes = connection.getInputStream().readAllBytes();
			Files.writeString(path, new String(bytes));
		}
		catch (Exception ex) {
			log.error("Error while fetching saints", ex);
			Files.writeString(path, "");
		}
	}

	@Override
	public Saint find() {
		try {
			var date = LocalDate.now();
			var formatted = String.format("saints-%s.txt", date);
			var path = Path.of(formatted);
			if (Files.notExists(path)) {
				fetchAndSave(path);
			}
			Files.exists(path);
			return Saint.builder().value(Files.readString(path)).build();
		}
		catch (Exception ex) {
			throw new FatalException(ex);
		}
	}

}
