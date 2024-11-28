package fr.damnardev.twitch.bot.database.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@AllArgsConstructor
@Builder(toBuilder = true)
@Data
@Entity
@NoArgsConstructor
@Table(name = "t_suggest_game")
public class DbSuggestGame {

	@Id
	private Long id;

	@OneToOne
	@JoinColumn(name = "id")
	@MapsId
	@ToString.Exclude
	@EqualsAndHashCode.Exclude
	private DbChannel channel;

	@Column(name = "form_id")
	private String formId;

	@Column(name = "viewer_field_id")
	private String viewerFieldId;

	@Column(name = "game_field_id")
	private String gameFieldId;

}
