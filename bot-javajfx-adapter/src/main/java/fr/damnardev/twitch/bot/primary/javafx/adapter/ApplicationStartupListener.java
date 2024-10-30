package fr.damnardev.twitch.bot.primary.javafx.adapter;

import java.io.IOException;
import java.util.Objects;

import fr.damnardev.twitch.bot.domain.exception.FatalException;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
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
//@ConditionalOnProperty(name = "javafx.enabled", havingValue = "true")
public class ApplicationStartupListener implements ApplicationRunner {

	private final ConfigurableApplicationContext springContext;

	@Override
	public void run(ApplicationArguments args) {
		log.info("Starting GUI");
		startup();
		log.info("GUI started");
	}

	private void startup() {
		Platform.startup(() -> {
			try {
				createWindow();
				Platform.setImplicitExit(true);
			}
			catch (IOException ex) {
				throw new FatalException(ex);
			}
		});
	}

	private void createWindow() throws IOException {
		var stage = new Stage();
		var scene = loadScene();
		stage.setScene(scene);
		setupStage(stage);
	}

	private void setupStage(Stage stage) {
		stage.setTitle("Twitch Bot");
		stage.setMinWidth(600);
		stage.setMinHeight(600);
		stage.setAlwaysOnTop(false);
		stage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/fr/damnardev/twitch/bot/primary/javafx/icon.png"))));
		stage.setOnCloseRequest((_) -> this.springContext.close());
		stage.show();
	}

	private Scene loadScene() throws IOException {
		var fxmlLoader = new FXMLLoader(getClass().getResource("/fr/damnardev/twitch/bot/primary/javafx/view/main.fxml"));
		fxmlLoader.setControllerFactory(this.springContext::getBean);
		return new Scene(fxmlLoader.load(), 600, 600);
	}

}
