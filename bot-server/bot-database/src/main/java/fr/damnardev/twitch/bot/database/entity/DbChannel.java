package fr.damnardev.twitch.bot.database.entity;

import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@Data
@Entity
@NoArgsConstructor
@Table(name = "t_channel")
public class DbChannel {

	@Id
	@Column(name = "id")
	private Long id;

	@Column(name = "name")
	private String name;

	@Column(name = "online")
	private boolean online;

	@Column(name = "bot_enabled")
	private boolean enabled;

	@OneToMany(cascade = CascadeType.REMOVE, mappedBy = "channel")
	private List<DbChannelCommand> commands;

	@OneToOne(cascade = CascadeType.ALL, mappedBy = "channel")
	private DbRaidConfiguration raidConfiguration;

}
