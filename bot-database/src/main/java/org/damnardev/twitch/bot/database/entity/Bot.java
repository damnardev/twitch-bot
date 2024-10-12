package org.damnardev.twitch.bot.database.entity;

import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "t_bot")
public class Bot {

    @Id
    private Long id;

    @OneToOne
    @JoinColumn(name = "id")
    @MapsId
    @ToString.Exclude
    private Channel channel;

    @Column(name = "enabled")
    private boolean enabled;

}
