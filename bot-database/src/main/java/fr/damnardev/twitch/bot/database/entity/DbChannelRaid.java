package fr.damnardev.twitch.bot.database.entity;

import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "t_channel_raid")
public class DbChannelRaid {

	@Id
	private Long id;

	@OneToOne
	@JoinColumn(name = "id")
	@MapsId
	@ToString.Exclude
	private DbChannel channel;

	@Column(name = "twitch_shoutout_enabled")
	private boolean twitchShoutoutEnabled;

	@Column(name = "wizebot_shoutout_enabled")
	private boolean wizebotShoutoutEnabled;

	@Column(name = "raid_message_enabled")
	private boolean raidMessageEnabled;

	@ElementCollection
	@CollectionTable(name = "t_channel_raid_message", joinColumns = @JoinColumn(name = "channel_raid_id"))
	@Column(name = "message")
	private List<String> messages;

}
