package com.blueharbor.hotel.app;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.Locale;
import java.util.ResourceBundle;

@Component
public class SpringFXMLLoader {

    private final ApplicationContext context;
    private final ResourceBundle bundle;

    public SpringFXMLLoader(ApplicationContext context) {
        this.context = context;
        this.bundle = ResourceBundle.getBundle("messages/strings", Locale.getDefault());
    }

    public Parent load(String path) {
        return loadInternal(path).view();
    }

    public LoadedView loadView(String path) {
        return loadInternal(path);
    }

    private LoadedView loadInternal(String path) {
        try {
            URL resource = getClass().getResource(path);
            if (resource == null) {
                throw new IllegalArgumentException("FXML not found: " + path);
            }
            FXMLLoader loader = new FXMLLoader(resource, bundle);
            loader.setControllerFactory(context::getBean);
            Parent view = loader.load();
            return new LoadedView(view, loader.getController());
        } catch (IOException e) {
            throw new IllegalStateException("Unable to load FXML " + path, e);
        }
    }

    public record LoadedView(Parent view, Object controller) {
    }
}
