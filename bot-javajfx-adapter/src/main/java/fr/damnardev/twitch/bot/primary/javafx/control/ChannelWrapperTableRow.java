package fr.damnardev.twitch.bot.primary.javafx.control;

import fr.damnardev.twitch.bot.primary.javafx.wrapper.ChannelWrapper;
import javafx.scene.control.TableRow;

public class ChannelWrapperTableRow extends TableRow<ChannelWrapper> {

	private static final String ENABLED_STYLE = "ChannelEnabled";

	private static final String ONLINE_STYLE = "ChannelOnline";

	@Override
	protected void updateItem(ChannelWrapper item, boolean empty) {
		super.updateItem(item, empty);
		if (item != null) {
			item.enabledProperty().addListener((_, _, _) -> this.updateRowColor(this.getItem()));
			item.onlineProperty().addListener((_, _, _) -> this.updateRowColor(this.getItem()));
		}
		updateRowColor(item);
	}

	private void updateRowColor(ChannelWrapper item) {
		getStyleClass().removeAll(ENABLED_STYLE, ONLINE_STYLE);
		if (item == null) {
			setStyle("");
		}
		else if (item.enabledProperty().get() && !item.onlineProperty().get()) {
			getStyleClass().add(ENABLED_STYLE);
		}
		else if (item.enabledProperty().get() && item.onlineProperty().get()) {
			getStyleClass().add(ONLINE_STYLE);
		}
	}

}
