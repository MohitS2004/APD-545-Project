package com.blueharbor.hotel.app;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class ViewNavigator {

    private final SpringFXMLLoader fxmlLoader;
    private Stage primaryStage;

    public ViewNavigator(SpringFXMLLoader fxmlLoader) {
        this.fxmlLoader = fxmlLoader;
    }

    public void registerPrimaryStage(Stage stage) {
        this.primaryStage = stage;
        this.primaryStage.setMinWidth(1200);
        this.primaryStage.setMinHeight(800);
    }

    public void goTo(ViewKey viewKey) {
        Objects.requireNonNull(primaryStage, "Stage not ready");
        Parent root = fxmlLoader.load(viewKey.fxml());
        Scene scene = primaryStage.getScene();
        if (scene == null) {
            scene = new Scene(root);
            primaryStage.setScene(scene);
        } else {
            scene.setRoot(root);
        }
        primaryStage.setTitle(viewKey.title());
        primaryStage.show();
    }
}
