package org.damnardev.twitch.bot.database.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.OffsetDateTime;
import java.util.List;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "t_channel_command")
public class ChannelCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "channel_id")
    @ToString.Exclude
    private Channel channel;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "name")
    private String name;

    @Column(name = "last_execution",
            columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime lastExecution;

    @Column(name = "cooldown")
    private long cooldown;

    @ElementCollection
    @CollectionTable(name = "t_channel_command_message",
                     joinColumns = @JoinColumn(name = "channel_command_id"))
    @Column(name = "message")
    private List<String> messages;

}
