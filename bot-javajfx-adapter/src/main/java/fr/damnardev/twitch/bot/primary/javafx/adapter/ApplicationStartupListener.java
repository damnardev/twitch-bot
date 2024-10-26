package fr.damnardev.twitch.bot.primary.javafx.adapter;

import java.io.IOException;

import fr.damnardev.twitch.bot.domain.exception.FatalException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class ApplicationStartupListener implements ApplicationRunner {

	private final ConfigurableApplicationContext springContext;

	@Override
	public void run(ApplicationArguments args) {
		log.info("Starting GUI");
		Platform.startup(() -> {
			try {
				createWindow();
				Platform.setImplicitExit(true);
			}
			catch (IOException ex) {
				throw new FatalException(ex);
			}
		});
		log.info("GUI started");
	}

	private void createWindow() throws IOException {
		var stage = new Stage();
		var fxmlLoader = new FXMLLoader(getClass().getResource("/fr/damnardev/twitch/bot/primary/javafx/view/bot-twitch-view.fxml"));
		fxmlLoader.setControllerFactory(this.springContext::getBean);
		var scene = new Scene(fxmlLoader.load(), 600, 600);
		stage.setTitle("Twitch Bot");
		stage.setScene(scene);
		stage.setMinWidth(600);
		stage.setMinHeight(600);
		stage.setAlwaysOnTop(false);
		stage.setOnCloseRequest((event) -> this.springContext.close());
		stage.show();
	}

}
