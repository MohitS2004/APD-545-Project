package com.blueharbor.hotel.controller.kiosk;

import com.blueharbor.hotel.config.ConfigRegistry;
import com.blueharbor.hotel.model.addon.AddOnCode;
import com.blueharbor.hotel.viewmodel.KioskWizardViewModel;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Component
@Scope("prototype")
public class AddOnStepController extends AbstractKioskStepController {

    @FXML
    private ListView<AddOnOption> addOnList;

    private final ConfigRegistry configRegistry;

    public AddOnStepController(ConfigRegistry configRegistry) {
        this.configRegistry = configRegistry;
    }

    @FXML
    public void initialize() {
        addOnList.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        addOnList.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(AddOnOption item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.label());
                }
            }
        });
    }

    @Override
    public void onBeforeShow() {
        Map<String, com.blueharbor.hotel.config.PricingProperties.AddOnDefinition> definitions = configRegistry.addOns();
        List<AddOnOption> options = definitions.entrySet().stream()
            .map(entry -> new AddOnOption(
                AddOnCode.valueOf(entry.getKey()),
                entry.getKey(),
                entry.getValue().getPrice(),
                entry.getValue().isPerNight()
            ))
            .toList();
        addOnList.setItems(FXCollections.observableArrayList(options));
        options.stream()
            .filter(option -> viewModel.getAddOnSelections().stream()
                .anyMatch(selected -> selected.getCode().equals(option.code().name())))
            .forEach(option -> addOnList.getSelectionModel().select(option));
    }

    @Override
    public boolean validateAndStore() {
        viewModel.getAddOnSelections().clear();
        addOnList.getSelectionModel().getSelectedItems().forEach(option -> {
            var selection = new KioskWizardViewModel.AddOnSelection();
            selection.setCode(option.code().name());
            selection.setName(option.name());
            selection.setPrice(option.price());
            selection.setPerNight(option.perNight());
            viewModel.getAddOnSelections().add(selection);
        });
        return true;
    }

    private record AddOnOption(AddOnCode code, String name, BigDecimal price, boolean perNight) {
        String label() {
            return name + " - $" + price + (perNight ? " /night" : "");
        }
    }
}
