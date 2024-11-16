package fr.damnardev.twitch.bot.secondary.adapter;

import fr.damnardev.twitch.bot.domain.model.event.ApplicationStartedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.context.ApplicationEventPublisher;

import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.WARN)
class DefaultEventPublisherTests {

	@InjectMocks
	private DefaultEventPublisher eventPublisher;

	@Mock
	private ApplicationEventPublisher publisher;

	@Test
	void publish_shouldInvokePublish_whenCalled() {
		// Given
		var event = ApplicationStartedEvent.builder().build();

		// When
		this.eventPublisher.publish(event);

		// Then
		then(this.publisher).should().publishEvent(event);
		verifyNoMoreInteractions(this.publisher);
	}

}
