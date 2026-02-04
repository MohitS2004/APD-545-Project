package com.blueharbor.hotel.controller;

import com.blueharbor.hotel.app.SpringFXMLLoader;
import com.blueharbor.hotel.app.ViewKey;
import com.blueharbor.hotel.app.ViewNavigator;
import com.blueharbor.hotel.dto.ReservationEstimate;
import com.blueharbor.hotel.dto.ReservationRequest;
import com.blueharbor.hotel.model.RoomType;
import com.blueharbor.hotel.model.addon.AddOnCode;
import com.blueharbor.hotel.service.PricingService;
import com.blueharbor.hotel.service.ReservationService;
import com.blueharbor.hotel.viewmodel.KioskWizardViewModel;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.StackPane;
import javafx.scene.media.MediaView;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class KioskController {

    @FXML
    private ProgressBar progressBar;
    @FXML
    private StackPane stepPane;
    @FXML
    private Button nextButton;
    @FXML
    private MediaView mediaView;

    private final SpringFXMLLoader fxmlLoader;
    private final ReservationService reservationService;
    private final PricingService pricingService;
    private final ViewNavigator navigator;

    private final List<com.blueharbor.hotel.controller.kiosk.KioskWizardStep> steps = new ArrayList<>();
    private int currentIndex;
    private KioskWizardViewModel viewModel = new KioskWizardViewModel();

    public KioskController(SpringFXMLLoader fxmlLoader,
                           ReservationService reservationService,
                           PricingService pricingService,
                           ViewNavigator navigator) {
        this.fxmlLoader = fxmlLoader;
        this.reservationService = reservationService;
        this.pricingService = pricingService;
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        loadStep("/view/kiosk/steps/kiosk-step-occupancy.fxml");
        loadStep("/view/kiosk/steps/kiosk-step-dates.fxml");
        loadStep("/view/kiosk/steps/kiosk-step-rooms.fxml");
        loadStep("/view/kiosk/steps/kiosk-step-guest.fxml");
        loadStep("/view/kiosk/steps/kiosk-step-addons.fxml");
        loadStep("/view/kiosk/steps/kiosk-step-review.fxml");
        showStep(0);
    }

    private void loadStep(String path) {
        SpringFXMLLoader.LoadedView loaded = fxmlLoader.loadView(path);
        Object controller = loaded.controller();
        if (!(controller instanceof com.blueharbor.hotel.controller.kiosk.KioskWizardStep step)) {
            throw new IllegalStateException("Step controller must implement KioskWizardStep: " + path);
        }
        if (controller instanceof com.blueharbor.hotel.controller.kiosk.AbstractKioskStepController abstractStep) {
            abstractStep.setView(loaded.view());
        }
        step.attach(viewModel);
        steps.add(step);
    }

    @FXML
    public void nextStep() {
        com.blueharbor.hotel.controller.kiosk.KioskWizardStep current = steps.get(currentIndex);
        if (!current.validateAndStore()) {
            return;
        }
        if (currentIndex == steps.size() - 1) {
            confirmReservation();
            return;
        }
        currentIndex++;
        if (isReviewStep()) {
            updateEstimate();
        }
        showStep(currentIndex);
    }

    @FXML
    public void previousStep() {
        if (currentIndex == 0) {
            return;
        }
        currentIndex--;
        showStep(currentIndex);
    }

    @FXML
    public void showRules() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Rules & Regulations");
        alert.setHeaderText("Stay Rules");
        alert.setContentText("""
            - No smoking in rooms
            - Check-in after 3PM, checkout before 11AM
            - Max occupancy enforced per room type
            """);
        alert.showAndWait();
    }

    private void showStep(int index) {
        com.blueharbor.hotel.controller.kiosk.KioskWizardStep step = steps.get(index);
        stepPane.getChildren().setAll(step.getView());
        step.onBeforeShow();
        progressBar.setProgress(index / (double) (steps.size() - 1));
        nextButton.setText(index == steps.size() - 1 ? "Confirm" : "Next");
    }

    private boolean isReviewStep() {
        return currentIndex == steps.size() - 1;
    }

    private void updateEstimate() {
        ReservationRequest request = toReservationRequest();
        ReservationEstimate estimate = pricingService.estimate(request);
        viewModel.setSubtotal(estimate.subtotal());
        viewModel.setTaxes(estimate.tax());
        viewModel.setTotal(estimate.total());
    }

    private void confirmReservation() {
        try {
            ReservationRequest request = toReservationRequest();
            reservationService.createReservation(request);
            showConfirmation();
            resetWizard();
        } catch (Exception ex) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not save reservation");
            alert.setContentText(ex.getMessage());
            alert.showAndWait();
        }
    }

    private void showConfirmation() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reservation Saved");
        alert.setHeaderText("You're all set!");
        alert.setContentText("Your reservation was submitted. Please visit the front desk for billing.");
        alert.showAndWait();
    }

    private void resetWizard() {
        currentIndex = 0;
        viewModel = new KioskWizardViewModel();
        steps.forEach(step -> step.attach(viewModel));
        showStep(0);
    }

    private ReservationRequest toReservationRequest() {
        Map<RoomType, Integer> roomCounts = new EnumMap<>(RoomType.class);
        viewModel.getRoomSelections().forEach((type, selection) -> roomCounts.put(type, selection.getQuantity()));
        List<AddOnCode> addOns = viewModel.getAddOnSelections().stream()
            .map(addOn -> AddOnCode.valueOf(addOn.getCode()))
            .toList();
        var guest = viewModel.getGuestDetails();
        return new ReservationRequest(
            guest.getFirstName(),
            guest.getLastName(),
            guest.getEmail(),
            guest.getPhone(),
            viewModel.getAdults(),
            viewModel.getChildren(),
            viewModel.getCheckInDate(),
            viewModel.getCheckOutDate(),
            roomCounts,
            addOns,
            viewModel.isEnrollInLoyalty(),
            guest.getNotes()
        );
    }

    @FXML
    public void exitToLogin() {
        navigator.goTo(ViewKey.WELCOME);
    }
}
