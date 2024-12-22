package fr.damnardev.twitch.bot.primary.javafx.control;

import javafx.scene.control.CheckBox;
import javafx.scene.control.cell.CheckBoxTableCell;

public class UnfocusableCheckBoxTableCell<S> extends CheckBoxTableCell<S, Boolean> {

	public UnfocusableCheckBoxTableCell() {
		super();
	}

	@Override
	public void updateItem(Boolean item, boolean isEmpty) {
		super.updateItem(item, isEmpty);
		if (!isEmpty && getGraphic() instanceof CheckBox checkBox) {
			checkBox.setFocusTraversable(false);
		}
	}

}
