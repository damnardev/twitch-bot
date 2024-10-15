package org.damnardev.twitch.bot.secondary.adapter;

import com.github.twitch4j.TwitchClient;
import com.github.twitch4j.eventsub.subscriptions.SubscriptionTypes;
import com.github.twitch4j.helix.domain.Stream;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.damnardev.twitch.bot.database.entity.Channel;
import org.damnardev.twitch.bot.database.repository.DbChannelRepository;
import org.damnardev.twitch.bot.domain.model.ChannelInfo;
import org.damnardev.twitch.bot.domain.port.secondary.IChannelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChannelRepository implements IChannelRepository {

    private final TwitchClient twitchClient;

    private final DbChannelRepository repository;

    @Override
    @Transactional
    public void computeStatus() {
        var channels = repository.findAllEnabled();
        if(channels.isEmpty()) {
            return;
        }
        var onLiveMap = getOnLive(channels.stream()
                                          .map(Channel::getName)
                                          .toList());
        channels.forEach(channel -> {
            var status = onLiveMap.getOrDefault(channel.getName(), false);
            channel.setOnline(status);
        });
        repository.saveAllAndFlush(channels);
    }

    public Map<String, Boolean> getOnLive(List<String> channels) {
        return twitchClient.getHelix()
                           .getStreams(null, null, null, null, null, null, null, channels)
                           .execute()
                           .getStreams()
                           .stream()
                           .collect(Collectors.toMap(Stream::getUserLogin, stream -> stream.getType()
                                                                                           .equalsIgnoreCase("live")));
    }

    @Override
    public ChannelInfo find(String channelName) {
        return repository.find(channelName)
                         .map(this::toModel)
                         .orElseGet(() -> ChannelInfo.builder()
                                                     .build());
    }

    private ChannelInfo toModel(Channel channel) {
        return ChannelInfo.builder()
                          .id(channel.getId())
                          .name(channel.getName())
                          .enabled(channel.isBotEnabled())
                          .online(channel.isOnline())
                          .build();
    }


    @Override
    public void joinAllChannel() {
        var channels = repository.findAllEnabled();
        if (channels.isEmpty()) {
            return;
        }
        joinChannel(channels);
        enableEventListener(channels);
    }

    private void joinChannel(List<Channel> channels) {
        channels.forEach(broadcaster -> twitchClient.getChat()
                                                    .joinChannel(broadcaster.getName()));
    }

    private void enableEventListener(List<Channel> channels) {
        channels.forEach(this::registerEventSocket);
    }

    private void registerEventSocket(Channel broadcaster) {
        var eventSocket = twitchClient.getEventSocket();
        eventSocket.register(SubscriptionTypes.STREAM_ONLINE.prepareSubscription(b -> b.broadcasterUserId(String.valueOf(broadcaster.getId()))
                                                                                       .build(), null));
        eventSocket.register(SubscriptionTypes.STREAM_OFFLINE.prepareSubscription(b -> b.broadcasterUserId(String.valueOf(broadcaster.getId()))
                                                                                        .build(), null));
        eventSocket.register(SubscriptionTypes.CHANNEL_RAID.prepareSubscription(b -> b.toBroadcasterUserId(String.valueOf(broadcaster.getId()))
                                                                                      .build(), null));
    }

    @Override
    public void reconnect() {
        twitchClient.getChat()
                    .reconnect();
    }

}
