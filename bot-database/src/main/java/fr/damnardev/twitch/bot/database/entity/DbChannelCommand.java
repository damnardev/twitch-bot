package fr.damnardev.twitch.bot.database.entity;

import java.time.OffsetDateTime;
import java.util.List;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "t_channel_command")
public class DbChannelCommand {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "channel_id")
	@ToString.Exclude
	private DbChannel channel;

	@Column(name = "enabled")
	private boolean enabled;

	@Column(name = "name")
	private String name;

	@Column(name = "last_execution", columnDefinition = "TIMESTAMP WITH TIME ZONE")
	private OffsetDateTime lastExecution;

	@Column(name = "cooldown")
	private long cooldown;

	@ElementCollection
	@CollectionTable(name = "t_channel_command_message", joinColumns = @JoinColumn(name = "channel_command_id"))
	@Column(name = "message")
	private List<String> messages;

}
