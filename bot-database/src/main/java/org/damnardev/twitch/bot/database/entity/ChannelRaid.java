package org.damnardev.twitch.bot.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "t_channel_raid")
public class ChannelRaid {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    @ToString.Exclude
    private Channel channel;

    @Column(name = "twitch_shoutout_enabled")
    private boolean twitchShoutoutEnabled;

    @Column(name = "wizebot_shoutout_enabled")
    private boolean wizebotShoutoutEnabled;

    @Column(name = "raid_message_enabled")
    private boolean raidMessageEnabled;

    @ElementCollection
    @CollectionTable(name = "t_channel_raid_message",
                     joinColumns = @JoinColumn(name = "channel_raid_id"))
    @Column(name = "message")
    private List<String> messages;

}
