package fr.damnardev.twitch.bot.database.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
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
@Table(name = "t_saint")
public class DbSaint {

	@Id
	@Column(name = "id")
	private LocalDate id;

	@Column(name = "message")
	private String message;

}
