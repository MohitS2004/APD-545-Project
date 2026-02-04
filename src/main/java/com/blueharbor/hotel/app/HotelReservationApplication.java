package com.blueharbor.hotel.app;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Bootstraps Spring and JavaFX in a single desktop process.
 */
public class HotelReservationApplication extends Application {

    private ConfigurableApplicationContext context;
    private ViewNavigator navigator;

    @Override
    public void init() {
        context = SpringApplication.run(SpringBootConfig.class);
        navigator = context.getBean(ViewNavigator.class);
    }

    @Override
    public void start(Stage stage) {
        navigator.registerPrimaryStage(stage);
        navigator.goTo(ViewKey.WELCOME);
    }

    @Override
    public void stop() {
        if (context != null) {
            context.close();
        }
        Platform.exit();
    }
}
