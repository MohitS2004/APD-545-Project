package com.blueharbor.hotel.controller.kiosk;

import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
@Scope("prototype")
public class DateStepController extends AbstractKioskStepController {

    @FXML
    private DatePicker checkInPicker;
    @FXML
    private DatePicker checkOutPicker;
    @FXML
    private Label dateErrorLabel;

    @Override
    public void onBeforeShow() {
        if (viewModel.getCheckInDate() != null) {
            checkInPicker.setValue(viewModel.getCheckInDate());
        } else {
            checkInPicker.setValue(LocalDate.now());
        }
        if (viewModel.getCheckOutDate() != null) {
            checkOutPicker.setValue(viewModel.getCheckOutDate());
        } else {
            checkOutPicker.setValue(LocalDate.now().plusDays(1));
        }
        dateErrorLabel.setVisible(false);
    }

    @Override
    public boolean validateAndStore() {
        LocalDate in = checkInPicker.getValue();
        LocalDate out = checkOutPicker.getValue();
        if (in == null || out == null || !out.isAfter(in)) {
            dateErrorLabel.setText("Check-out must be after check-in.");
            dateErrorLabel.setVisible(true);
            return false;
        }
        viewModel.setCheckInDate(in);
        viewModel.setCheckOutDate(out);
        return true;
    }
}
