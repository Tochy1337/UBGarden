package fr.ubx.poo.ugarden.view;

import fr.ubx.poo.ugarden.Functions;
import fr.ubx.poo.ugarden.engine.GameEngine;
import fr.ubx.poo.ugarden.game.Game;
import fr.ubx.poo.ugarden.launcher.GameLauncher;
import fr.ubx.poo.ugarden.launcher.MapEntity;
import fr.ubx.poo.ugarden.launcher.MapLevel;
import fr.ubx.poo.ugarden.launcher.MapLevelDefault;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class GameLauncherView extends BorderPane {
    private final FileChooser fileChooser = new FileChooser();

    public GameLauncherView(Stage stage) {
        // Create menu
        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem loadItem = new MenuItem("Load from file ...");
        MenuItem defaultItem = new MenuItem("Load default configuration");
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.setAccelerator(KeyCombination.keyCombination("Ctrl+Q"));
        menuFile.getItems().addAll(
                loadItem, defaultItem, new SeparatorMenuItem(),
                exitItem);

        menuBar.getMenus().addAll(menuFile);
        this.setTop(menuBar);

        Text text = new Text("UBGarden 2024");
        text.getStyleClass().add("message");
        VBox scene = new VBox();
        scene.getChildren().add(text);
        scene.getStylesheets().add(getClass().getResource("/css/application.css").toExternalForm());
        scene.getStyleClass().add("message");
        this.setCenter(scene);

        // Load from file
        loadItem.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(stage);
            if (file != null) {
                Game game = null;
                try {
                    game = GameLauncher.getInstance().load(file);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                GameEngine engine = new GameEngine(game, stage);
                engine.start();
            }
        });

        defaultItem.setOnAction(e -> {
            MapLevel mapLevel = new MapLevelDefault();
            Game game = GameLauncher.getInstance().load(mapLevel,1);
            GameEngine engine = new GameEngine(game, stage);
            engine.start();
        });

        // Exit
        exitItem.setOnAction(e -> System.exit(0));

    }


}
