package fr.damnardev.twitch.bot.primary.javafx.control;

import java.util.function.Consumer;

import javafx.scene.control.Button;
import javafx.scene.control.TableCell;

public class UnfocusableButtonTableCell<S> extends TableCell<S, String> {

	private final Button button = new Button("Delete");

	private final Consumer<S> consumer;

	public UnfocusableButtonTableCell(Consumer<S> consumer) {
		this.consumer = consumer;
	}

	@Override
	protected void updateItem(String item, boolean empty) {
		super.updateItem(item, empty);
		if (empty) {
			setText(null);
			setGraphic(null);
		}
		else {
			var value = getTableView().getItems().get(getIndex());
			this.button.setOnMouseClicked((_) -> this.consumer.accept(value));
			this.button.setMaxWidth(Double.MAX_VALUE);
			this.button.setMaxHeight(Double.MAX_VALUE);
			this.button.setFocusTraversable(false);
			setGraphic(this.button);
		}
	}

}
