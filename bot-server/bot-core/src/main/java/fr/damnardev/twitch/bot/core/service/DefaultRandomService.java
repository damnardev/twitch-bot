package fr.damnardev.twitch.bot.core.service;

import java.util.List;
import java.util.Random;

import fr.damnardev.twitch.bot.DomainService;
import fr.damnardev.twitch.bot.port.primary.RandomService;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class DefaultRandomService implements RandomService {

	private final Random random;

	@Override
	public <T> T getRandom(List<T> values) {
		int index = this.random.nextInt(values.size());
		return values.get(index);
	}

}
