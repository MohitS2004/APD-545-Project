package com.blueharbor.hotel.controller;

import com.blueharbor.hotel.app.ViewKey;
import com.blueharbor.hotel.app.ViewNavigator;
import com.blueharbor.hotel.service.AuthenticationService;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class LoginController {

    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label errorLabel;

    private final AuthenticationService authenticationService;
    private final ViewNavigator navigator;

    public LoginController(AuthenticationService authenticationService, ViewNavigator navigator) {
        this.authenticationService = authenticationService;
        this.navigator = navigator;
    }

    @FXML
    public void handleLogin() {
        errorLabel.setVisible(false);
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        boolean authenticated = authenticationService.authenticate(email, password);
        if (authenticated) {
            navigator.goTo(ViewKey.ADMIN);
        } else {
            errorLabel.setText("Invalid credentials. Please try again.");
            errorLabel.setVisible(true);
        }
    }

    @FXML
    public void handleBackToWelcome() {
        navigator.goTo(ViewKey.WELCOME);
    }
}
