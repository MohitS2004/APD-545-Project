package com.blueharbor.hotel.controller.kiosk;

import com.blueharbor.hotel.config.ConfigRegistry;
import com.blueharbor.hotel.dto.RoomPlanSuggestion;
import com.blueharbor.hotel.model.RoomType;
import com.blueharbor.hotel.service.ReservationService;
import com.blueharbor.hotel.viewmodel.KioskWizardViewModel;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.Map;

@Component
@Scope("prototype")
public class RoomStepController extends AbstractKioskStepController {

    @FXML
    private TableView<RoomRow> roomPlanTable;
    @FXML
    private ChoiceBox<RoomType> roomTypeChoice;
    @FXML
    private Spinner<Integer> roomQuantitySpinner;
    @FXML
    private Label errorLabel;

    private final ReservationService reservationService;
    private final ConfigRegistry configRegistry;
    private final ObservableList<RoomRow> rows = FXCollections.observableArrayList();

    public RoomStepController(ReservationService reservationService, ConfigRegistry configRegistry) {
        this.reservationService = reservationService;
        this.configRegistry = configRegistry;
    }

    @FXML
    public void initialize() {
        TableColumn<RoomRow, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("typeLabel"));
        TableColumn<RoomRow, Integer> qtyCol = new TableColumn<>("Quantity");
        qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        TableColumn<RoomRow, Integer> capacityCol = new TableColumn<>("Guests Supported");
        capacityCol.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        TableColumn<RoomRow, String> rateCol = new TableColumn<>("Nightly Rate");
        rateCol.setCellValueFactory(new PropertyValueFactory<>("rateLabel"));

        roomPlanTable.getColumns().setAll(typeCol, qtyCol, capacityCol, rateCol);
        roomPlanTable.setItems(rows);

        roomTypeChoice.setItems(FXCollections.observableArrayList(RoomType.values()));
        roomTypeChoice.getSelectionModel().select(RoomType.SINGLE);
        roomQuantitySpinner.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 5, 1));
    }

    @Override
    public void onBeforeShow() {
        rows.clear();
        if (viewModel.getRoomSelections().isEmpty()) {
            RoomPlanSuggestion suggestion = reservationService.suggestPlan(viewModel.getAdults(), viewModel.getChildren());
            suggestion.roomCounts().forEach((type, quantity) ->
                rows.add(RoomRow.of(type, quantity, configRegistry))
            );
        } else {
            viewModel.getRoomSelections().forEach((type, selection) ->
                rows.add(RoomRow.of(type, selection.getQuantity(), configRegistry))
            );
        }
        errorLabel.setVisible(false);
    }

    @Override
    public boolean validateAndStore() {
        Map<RoomType, Integer> counts = new EnumMap<>(RoomType.class);
        rows.forEach(row -> counts.put(row.getRoomType(), row.getQuantity()));
        if (counts.isEmpty()) {
            errorLabel.setText("Please select at least one room.");
            errorLabel.setVisible(true);
            return false;
        }
        viewModel.getRoomSelections().clear();
        counts.forEach((type, qty) -> {
            var selection = new KioskWizardViewModel.RoomSelection();
            selection.setRoomType(type);
            selection.setQuantity(qty);
            viewModel.getRoomSelections().put(type, selection);
        });
        errorLabel.setVisible(false);
        return true;
    }

    @FXML
    public void handleAddRoom() {
        RoomType type = roomTypeChoice.getValue();
        int qty = roomQuantitySpinner.getValue();
        rows.removeIf(row -> row.getRoomType() == type);
        rows.add(RoomRow.of(type, qty, configRegistry));
    }

    @FXML
    public void openPolicyDialog() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Room Policy");
        alert.setHeaderText("Booking Policy");
        alert.setContentText("""
            Single: up to 2 guests
            Double: up to 4 guests
            Deluxe/Penthouse: up to 2 guests
            """);
        alert.showAndWait();
    }

    public static class RoomRow {
        private final RoomType roomType;
        private final int quantity;
        private final int capacity;
        private final double rate;

        private RoomRow(RoomType roomType, int quantity, int capacity, double rate) {
            this.roomType = roomType;
            this.quantity = quantity;
            this.capacity = capacity;
            this.rate = rate;
        }

        public static RoomRow of(RoomType type, int quantity, ConfigRegistry registry) {
            int cap = registry.occupancyLimit(type.name());
            double rate = registry.pricing().getBaseRates().get(type.name()).doubleValue();
            return new RoomRow(type, quantity, cap * quantity, rate);
        }

        public RoomType getRoomType() {
            return roomType;
        }

        public String getTypeLabel() {
            return roomType.name();
        }

        public int getQuantity() {
            return quantity;
        }

        public int getCapacity() {
            return capacity;
        }

        public String getRateLabel() {
            return "$" + rate;
        }
    }
}
