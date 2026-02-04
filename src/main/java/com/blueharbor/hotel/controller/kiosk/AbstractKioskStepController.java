package com.blueharbor.hotel.controller.kiosk;

import com.blueharbor.hotel.viewmodel.KioskWizardViewModel;
import javafx.scene.Parent;

public abstract class AbstractKioskStepController implements KioskWizardStep {

    protected KioskWizardViewModel viewModel;
    private Parent view;

    @Override
    public void attach(KioskWizardViewModel viewModel) {
        this.viewModel = viewModel;
    }

    @Override
    public Parent getView() {
        return view;
    }

    public void setView(Parent view) {
        this.view = view;
    }
}
