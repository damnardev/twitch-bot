package fr.damnardev.twitch.bot.domain.service;

import java.util.List;
import java.util.Random;

import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.port.primary.IRandomService;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class RandomService implements IRandomService {

	private final Random random;

	@Override
	public <T> T getRandom(List<T> values) {
		int index = this.random.nextInt(values.size());
		return values.get(index);
	}

}
