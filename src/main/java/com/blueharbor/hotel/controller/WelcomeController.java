package com.blueharbor.hotel.controller;

import com.blueharbor.hotel.app.ViewKey;
import com.blueharbor.hotel.app.ViewNavigator;
import javafx.fxml.FXML;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class WelcomeController {

    private final ViewNavigator navigator;

    public WelcomeController(ViewNavigator navigator) {
        this.navigator = navigator;
    }

    @FXML
    public void handleAdminLogin() {
        navigator.goTo(ViewKey.LOGIN);
    }

    @FXML
    public void handleGuestBooking() {
        navigator.goTo(ViewKey.KIOSK);
    }

    @FXML
    public void handleFeedback() {
        navigator.goTo(ViewKey.FEEDBACK);
    }
}
