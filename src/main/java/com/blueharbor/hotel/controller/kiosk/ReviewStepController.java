package com.blueharbor.hotel.controller.kiosk;

import com.blueharbor.hotel.viewmodel.KioskWizardViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Scope("prototype")
public class ReviewStepController extends AbstractKioskStepController {

    @FXML
    private ListView<String> summaryList;
    @FXML
    private Label totalLabel;

    @Override
    public void onBeforeShow() {
        List<String> lines = new ArrayList<>();
        lines.add("Guests: " + viewModel.getAdults() + " adults / " + viewModel.getChildren() + " children");
        lines.add("Dates: " + viewModel.getCheckInDate() + " -> " + viewModel.getCheckOutDate());
        viewModel.getRoomSelections().forEach((type, selection) ->
            lines.add(selection.getQuantity() + " x " + type.name())
        );
        if (!viewModel.getAddOnSelections().isEmpty()) {
            lines.add("Add-ons:");
            viewModel.getAddOnSelections().forEach(addOn ->
                lines.add("- " + addOn.getName() + " $" + addOn.getPrice())
            );
        }
        summaryList.setItems(FXCollections.observableArrayList(lines));
        totalLabel.setText("Estimated total: $" + viewModel.getTotal());
    }

    @Override
    public boolean validateAndStore() {
        // nothing to validate in the review step
        return true;
    }
}
