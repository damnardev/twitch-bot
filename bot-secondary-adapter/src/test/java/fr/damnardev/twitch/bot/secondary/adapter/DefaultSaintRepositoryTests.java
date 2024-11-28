package fr.damnardev.twitch.bot.secondary.adapter;

import java.time.LocalDate;
import java.util.Optional;

import fr.damnardev.twitch.bot.database.entity.DbSaint;
import fr.damnardev.twitch.bot.database.repository.DbSaintRepository;
import fr.damnardev.twitch.bot.exception.FatalException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultSaintRepositoryTests {

	@InjectMocks
	private DefaultSaintRepository saintRepository;

	@Mock
	private DbSaintRepository dbSaintRepository;

	@Mock
	private RestTemplate restTemplate;

	@Test
	void find_shouldReturnMessage_whenMessageIsPresent() {
		// Given
		var today = LocalDate.now();
		given(this.dbSaintRepository.findById(today)).willReturn(Optional.of(DbSaint.builder().message("message").build()));

		// When
		var result = this.saintRepository.find();

		// Then
		then(this.dbSaintRepository).should().findById(today);
		verifyNoMoreInteractions(this.dbSaintRepository, this.restTemplate);

		assertThat(result).isPresent().hasValue("message");
	}

	@Test
	void find_shouldThrowFatalException_whenExceptionIsThrown() {
		// Given
		var today = LocalDate.now();
		given(this.dbSaintRepository.findById(today)).willThrow(new RuntimeException());

		// When
		var result = assertThatThrownBy(() -> this.saintRepository.find());

		// Then
		then(this.dbSaintRepository).should().findById(today);
		verifyNoMoreInteractions(this.dbSaintRepository, this.restTemplate);

		result.isInstanceOf(FatalException.class);
	}

}
