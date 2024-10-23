package fr.damnardev.twitch.bot.secondary.adapter;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.helix.domain.User;
import com.github.twitch4j.helix.domain.UserList;
import fr.damnardev.twitch.bot.database.entity.Channel;
import fr.damnardev.twitch.bot.database.repository.DbChannelRepository;
import fr.damnardev.twitch.bot.domain.exception.FatalException;
import fr.damnardev.twitch.bot.domain.model.ChannelInfo;
import fr.damnardev.twitch.bot.domain.port.secondary.ISaveChannelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Slf4j
public class SaveChannelRepository implements ISaveChannelRepository {

    private final DbChannelRepository repository;

    private final TwitchClient twitchClient;

    @Override
    @Transactional
    public ChannelInfo save(ChannelInfo channel) {
        log.info("Saving channel {}", channel.user().name());
        var user = channel.user();
        var id = findUser(user).map(User::getId).map(Long::parseLong).orElseThrow(() -> new FatalException("User not found"));
        var result = Channel.builder().id(id).name(channel.user().name()).build();
        result = repository.save(result);
        log.info("Channel {} saved", channel.user().name());
        return toModel(result);
    }

    private @NotNull Optional<User> findUser(fr.damnardev.twitch.bot.domain.model.User user) {
        return callUserApi(user).getUsers().stream().findFirst();
    }

    private UserList callUserApi(fr.damnardev.twitch.bot.domain.model.User user) {
        var userNames = Collections.singletonList(user.name());
        return twitchClient.getHelix().getUsers(null, null, userNames).execute();
    }

    private ChannelInfo toModel(Channel channel) {
        var user = fr.damnardev.twitch.bot.domain.model.User.builder().id(channel.getId()).name(channel.getName()).build();
        return ChannelInfo.builder().user(user).enabled(channel.isBotEnabled()).online(channel.isOnline()).build();
    }

}
