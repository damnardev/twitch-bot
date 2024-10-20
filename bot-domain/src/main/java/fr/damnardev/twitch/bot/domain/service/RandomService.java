package fr.damnardev.twitch.bot.domain.service;

import lombok.RequiredArgsConstructor;
import fr.damnardev.twitch.bot.domain.DomainService;
import fr.damnardev.twitch.bot.domain.port.primary.IRandomService;

import java.util.List;
import java.util.Random;

@DomainService
@RequiredArgsConstructor
public class RandomService implements IRandomService {

    private final Random random;

    @Override
    public <T> T getRandom(List<T> values) {
        int index = random.nextInt(values.size());
        return values.get(index);
    }

}
