package com.blueharbor.hotel.controller.kiosk;

import com.blueharbor.hotel.util.ValidationUtil;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class GuestStepController extends AbstractKioskStepController {

    @FXML
    private TextField firstNameField;
    @FXML
    private TextField lastNameField;
    @FXML
    private TextField emailField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextArea notesArea;
    @FXML
    private Label guestErrorLabel;
    @FXML
    private CheckBox loyaltyCheckBox;

    @Override
    public void onBeforeShow() {
        if (viewModel.getGuestDetails() != null) {
            var guest = viewModel.getGuestDetails();
            firstNameField.setText(guest.getFirstName());
            lastNameField.setText(guest.getLastName());
            emailField.setText(guest.getEmail());
            phoneField.setText(guest.getPhone());
            notesArea.setText(guest.getNotes());
        }
        loyaltyCheckBox.setSelected(viewModel.isEnrollInLoyalty());
        guestErrorLabel.setVisible(false);
    }

    @Override
    public boolean validateAndStore() {
        if (isBlank(firstNameField) || isBlank(lastNameField) || isBlank(emailField)) {
            guestErrorLabel.setText("Please fill in all required fields.");
            guestErrorLabel.setVisible(true);
            return false;
        }
        if (!ValidationUtil.isEmail(emailField.getText())) {
            guestErrorLabel.setText("Enter a valid email.");
            guestErrorLabel.setVisible(true);
            return false;
        }
        if (!ValidationUtil.isPhone(phoneField.getText())) {
            guestErrorLabel.setText("Enter a valid phone number.");
            guestErrorLabel.setVisible(true);
            return false;
        }
        var guest = viewModel.getGuestDetails();
        guest.setFirstName(firstNameField.getText());
        guest.setLastName(lastNameField.getText());
        guest.setEmail(emailField.getText());
        guest.setPhone(phoneField.getText());
        guest.setNotes(notesArea.getText());
        viewModel.setEnrollInLoyalty(loyaltyCheckBox.isSelected());
        guestErrorLabel.setVisible(false);
        return true;
    }

    private boolean isBlank(TextField field) {
        return field.getText() == null || field.getText().isBlank();
    }
}
