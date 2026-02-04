package com.blueharbor.hotel.controller.kiosk;

import com.blueharbor.hotel.viewmodel.KioskWizardViewModel;
import javafx.scene.Parent;

public interface KioskWizardStep {

    void attach(KioskWizardViewModel viewModel);

    /**
     * Called before the step becomes visible so it can refresh bindings.
     */
    default void onBeforeShow() {
        // optional hook
    }

    /**
     * Validates the UI state and syncs with the view model.
     *
     * @return true when the step content is valid
     */
    boolean validateAndStore();

    Parent getView();
}
