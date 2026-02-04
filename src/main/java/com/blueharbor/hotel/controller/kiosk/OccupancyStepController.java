package com.blueharbor.hotel.controller.kiosk;

import com.blueharbor.hotel.viewmodel.KioskWizardViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class OccupancyStepController extends AbstractKioskStepController {

    @FXML
    private Spinner<Integer> adultSpinner;
    @FXML
    private Spinner<Integer> childSpinner;
    @FXML
    private Label errorLabel;

    @FXML
    public void initialize() {
        adultSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 10, 1));
        childSpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 10, 0));
    }

    @Override
    public void onBeforeShow() {
        adultSpinner.getValueFactory().setValue(viewModel.getAdults());
        childSpinner.getValueFactory().setValue(viewModel.getChildren());
        if (errorLabel != null) {
            errorLabel.setVisible(false);
        }
    }

    @Override
    public boolean validateAndStore() {
        int adults = adultSpinner.getValue();
        if (adults < 1) {
            if (errorLabel != null) {
                errorLabel.setText("Need at least one adult.");
                errorLabel.setVisible(true);
            }
            return false;
        }
        viewModel.setAdults(adults);
        viewModel.setChildren(childSpinner.getValue());
        return true;
    }
}
