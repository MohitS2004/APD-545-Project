package com.blueharbor.hotel.controller;

import com.blueharbor.hotel.app.ViewKey;
import com.blueharbor.hotel.app.ViewNavigator;
import com.blueharbor.hotel.dto.ActivityLogFilter;
import com.blueharbor.hotel.dto.OccupancyReportFilter;
import com.blueharbor.hotel.dto.PaymentCommand;
import com.blueharbor.hotel.dto.ReservationRequest;
import com.blueharbor.hotel.dto.RevenueReportFilter;
import com.blueharbor.hotel.dto.WaitlistRequest;
import com.blueharbor.hotel.model.Reservation;
import com.blueharbor.hotel.model.ReservationStatus;
import com.blueharbor.hotel.model.RoomType;
import com.blueharbor.hotel.model.ReservedRoom;
import com.blueharbor.hotel.model.billing.Payment;
import com.blueharbor.hotel.model.billing.PaymentMethod;
import com.blueharbor.hotel.model.feedback.Feedback;
import com.blueharbor.hotel.model.report.ActivityRow;
import com.blueharbor.hotel.model.report.OccupancyRow;
import com.blueharbor.hotel.model.report.RevenueRow;
import com.blueharbor.hotel.model.waitlist.WaitlistEntry;
import com.blueharbor.hotel.service.AuthenticationService;
import com.blueharbor.hotel.service.ExportService;
import com.blueharbor.hotel.service.FeedbackService;
import com.blueharbor.hotel.service.PaymentService;
import com.blueharbor.hotel.service.ReportingService;
import com.blueharbor.hotel.service.ReservationService;
import com.blueharbor.hotel.service.WaitlistService;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
@Scope("prototype")
public class AdminDashboardController {

    @FXML private TextField searchField;
    @FXML private DatePicker fromDatePicker;
    @FXML private DatePicker toDatePicker;
    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, String> confirmationColumn;
    @FXML private TableColumn<Reservation, String> guestColumn;
    @FXML private TableColumn<Reservation, String> dateColumn;
    @FXML private TableColumn<Reservation, String> roomsColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, String> balanceColumn;

    @FXML private TableView<Payment> paymentTable;
    @FXML private TableColumn<Payment, String> paymentTimeColumn;
    @FXML private TableColumn<Payment, String> paymentReservationColumn;
    @FXML private TableColumn<Payment, String> paymentMethodColumn;
    @FXML private TableColumn<Payment, String> paymentAmountColumn;
    @FXML private TableColumn<Payment, String> paymentActorColumn;

    @FXML private TableView<WaitlistEntry> waitlistTable;
    @FXML private TableColumn<WaitlistEntry, String> waitlistGuestColumn;
    @FXML private TableColumn<WaitlistEntry, String> waitlistRoomColumn;
    @FXML private TableColumn<WaitlistEntry, String> waitlistDatesColumn;
    @FXML private TableColumn<WaitlistEntry, String> waitlistStatusColumn;

    @FXML private ChoiceBox<String> ratingFilter;
    @FXML private DatePicker feedbackFromDate;
    @FXML private DatePicker feedbackToDate;
    @FXML private TableView<Feedback> feedbackTable;
    @FXML private TableColumn<Feedback, String> feedbackGuestColumn;
    @FXML private TableColumn<Feedback, Number> feedbackRatingColumn;
    @FXML private TableColumn<Feedback, String> feedbackCommentColumn;
    @FXML private TableColumn<Feedback, String> feedbackDateColumn;

    @FXML private TableView<RevenueRow> revenueTable;
    @FXML private TableColumn<RevenueRow, String> revenuePeriodColumn;
    @FXML private TableColumn<RevenueRow, Number> revenueCountColumn;
    @FXML private TableColumn<RevenueRow, String> revenueSubtotalColumn;
    @FXML private TableColumn<RevenueRow, String> revenueTaxColumn;
    @FXML private TableColumn<RevenueRow, String> revenueDiscountColumn;
    @FXML private TableColumn<RevenueRow, String> revenueTotalColumn;

    @FXML private TableView<OccupancyRow> occupancyTable;
    @FXML private TableColumn<OccupancyRow, String> occupancyDateColumn;
    @FXML private TableColumn<OccupancyRow, Number> occupancyAvailableColumn;
    @FXML private TableColumn<OccupancyRow, Number> occupancyOccupiedColumn;
    @FXML private TableColumn<OccupancyRow, Number> occupancyPercentColumn;

    @FXML private TableView<ActivityRow> activityTable;
    @FXML private TableColumn<ActivityRow, String> activityTimeColumn;
    @FXML private TableColumn<ActivityRow, String> activityActorColumn;
    @FXML private TableColumn<ActivityRow, String> activityActionColumn;
    @FXML private TableColumn<ActivityRow, String> activityEntityColumn;
    @FXML private TableColumn<ActivityRow, String> activityMessageColumn;

    private final ReservationService reservationService;
    private final PaymentService paymentService;
    private final WaitlistService waitlistService;
    private final FeedbackService feedbackService;
    private final ReportingService reportingService;
    private final ExportService exportService;
    private final AuthenticationService authenticationService;
    private final ViewNavigator navigator;

    private final ObservableList<Reservation> reservationItems = FXCollections.observableArrayList();
    private final ObservableList<Payment> paymentItems = FXCollections.observableArrayList();
    private final ObservableList<WaitlistEntry> waitlistItems = FXCollections.observableArrayList();
    private final ObservableList<Feedback> feedbackItems = FXCollections.observableArrayList();
    private final ObservableList<RevenueRow> revenueItems = FXCollections.observableArrayList();
    private final ObservableList<OccupancyRow> occupancyItems = FXCollections.observableArrayList();
    private final ObservableList<ActivityRow> activityItems = FXCollections.observableArrayList();

    public AdminDashboardController(
        ReservationService reservationService,
        PaymentService paymentService,
        WaitlistService waitlistService,
        FeedbackService feedbackService,
        ReportingService reportingService,
        ExportService exportService,
        AuthenticationService authenticationService,
        ViewNavigator navigator
    ) {
        this.reservationService = reservationService;
        this.paymentService = paymentService;
        this.waitlistService = waitlistService;
        this.feedbackService = feedbackService;
        this.reportingService = reportingService;
        this.exportService = exportService;
        this.authenticationService = authenticationService;
        this.navigator = navigator;
    }

    @FXML
    public void initialize() {
        configureTables();
        ratingFilter.setItems(FXCollections.observableArrayList("All", "5", "4", "3", "2", "1"));
        ratingFilter.getSelectionModel().select("All");
        ratingFilter.setOnAction(event -> loadFeedback());
        feedbackFromDate.valueProperty().addListener((obs, oldVal, newVal) -> loadFeedback());
        feedbackToDate.valueProperty().addListener((obs, oldVal, newVal) -> loadFeedback());
        fromDatePicker.setValue(LocalDate.now().minusMonths(3));
        toDatePicker.setValue(LocalDate.now().plusYears(1));
        feedbackFromDate.setValue(LocalDate.now().minusMonths(1));
        feedbackToDate.setValue(LocalDate.now());
        reloadAll();
    }

    private void configureTables() {
        confirmationColumn.setCellValueFactory(row -> fx(row.getValue().getConfirmationCode()));
        guestColumn.setCellValueFactory(row -> fx(row.getValue().getGuest().getFirstName() + " " + row.getValue().getGuest().getLastName()));
        dateColumn.setCellValueFactory(row -> fx(row.getValue().getCheckInDate() + " → " + row.getValue().getCheckOutDate()));
        roomsColumn.setCellValueFactory(row -> fx(String.valueOf(row.getValue().getRooms().stream()
            .mapToInt(ReservedRoom::getQuantity)
            .sum())));
        statusColumn.setCellValueFactory(row -> fx(row.getValue().getStatus().name()));
        balanceColumn.setCellValueFactory(row -> fx("$" + outstanding(row.getValue())));
        reservationTable.setItems(reservationItems);

        paymentTimeColumn.setCellValueFactory(row -> fx(row.getValue().getProcessedAt().toString()));
        paymentReservationColumn.setCellValueFactory(row -> fx(row.getValue().getReservation().getConfirmationCode()));
        paymentMethodColumn.setCellValueFactory(row -> fx(row.getValue().getMethod().name()));
        paymentAmountColumn.setCellValueFactory(row -> fx(row.getValue().getAmount().toString()));
        paymentActorColumn.setCellValueFactory(new PropertyValueFactory<>("actorEmail"));
        paymentTable.setItems(paymentItems);

        waitlistGuestColumn.setCellValueFactory(row -> fx(row.getValue().getGuestName()));
        waitlistRoomColumn.setCellValueFactory(row -> fx(row.getValue().getDesiredRoomType().name()));
        waitlistDatesColumn.setCellValueFactory(row -> fx(row.getValue().getStartDate() + " → " + row.getValue().getEndDate()));
        waitlistStatusColumn.setCellValueFactory(row -> fx(row.getValue().getStatus().name()));
        waitlistTable.setItems(waitlistItems);

        feedbackGuestColumn.setCellValueFactory(row -> fx(row.getValue().getReservation().getGuest().getFirstName()));
        feedbackRatingColumn.setCellValueFactory(new PropertyValueFactory<>("rating"));
        feedbackCommentColumn.setCellValueFactory(new PropertyValueFactory<>("comment"));
        feedbackDateColumn.setCellValueFactory(row -> fx(row.getValue().getSubmittedAt().toLocalDate().toString()));
        feedbackTable.setItems(feedbackItems);

        revenuePeriodColumn.setCellValueFactory(new PropertyValueFactory<>("periodLabel"));
        revenueCountColumn.setCellValueFactory(new PropertyValueFactory<>("reservations"));
        revenueSubtotalColumn.setCellValueFactory(row -> fx(row.getValue().subtotal().toString()));
        revenueTaxColumn.setCellValueFactory(row -> fx(row.getValue().tax().toString()));
        revenueDiscountColumn.setCellValueFactory(row -> fx(row.getValue().discounts().toString()));
        revenueTotalColumn.setCellValueFactory(row -> fx(row.getValue().total().toString()));
        revenueTable.setItems(revenueItems);

        occupancyDateColumn.setCellValueFactory(row -> fx(row.getValue().date().toString()));
        occupancyAvailableColumn.setCellValueFactory(new PropertyValueFactory<>("roomsAvailable"));
        occupancyOccupiedColumn.setCellValueFactory(new PropertyValueFactory<>("roomsOccupied"));
        occupancyPercentColumn.setCellValueFactory(new PropertyValueFactory<>("occupancyPercentage"));
        occupancyTable.setItems(occupancyItems);

        activityTimeColumn.setCellValueFactory(row -> fx(row.getValue().timestamp().toString()));
        activityActorColumn.setCellValueFactory(new PropertyValueFactory<>("actor"));
        activityActionColumn.setCellValueFactory(new PropertyValueFactory<>("action"));
        activityEntityColumn.setCellValueFactory(new PropertyValueFactory<>("entityType"));
        activityMessageColumn.setCellValueFactory(new PropertyValueFactory<>("message"));
        activityTable.setItems(activityItems);
    }

    private void reloadAll() {
        runReservationSearch();
        loadPayments();
        loadWaitlist();
        loadFeedback();
        loadReports();
        loadActivity();
    }

    @FXML
    public void runReservationSearch() {
        List<Reservation> results = reservationService.search(
            null,
            blank(searchField.getText()),
            null,
            fromDatePicker.getValue(),
            toDatePicker.getValue()
        );
        reservationItems.setAll(results);
    }

    @FXML
    public void openManualReservation() {
        Dialog<ReservationRequest> dialog = new Dialog<>();
        dialog.setTitle("Phone Reservation");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        TextField first = new TextField();
        TextField last = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        DatePicker checkIn = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker checkOut = new DatePicker(LocalDate.now().plusDays(2));
        TextField adults = new TextField("2");
        TextField children = new TextField("0");
        grid.addRow(0, first, last);
        grid.addRow(1, email, phone);
        grid.addRow(2, checkIn, checkOut);
        grid.addRow(3, adults, children);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                int adultCount = Integer.parseInt(adults.getText());
                RoomType suggested = adultCount > 2 ? RoomType.DOUBLE : RoomType.SINGLE;
                return new ReservationRequest(
                    first.getText(),
                    last.getText(),
                    email.getText(),
                    phone.getText(),
                    adultCount,
                    Integer.parseInt(children.getText()),
                    checkIn.getValue(),
                    checkOut.getValue(),
                    Map.of(suggested, 1),
                    List.of(),
                    false,
                    ""
                );
            }
            return null;
        });
        Optional<ReservationRequest> result = dialog.showAndWait();
        result.ifPresent(reservationService::createReservation);
        reloadAll();
    }

    @FXML
    public void openReservationDetail() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Reservation Detail");
        alert.setHeaderText(selected.getConfirmationCode());
        alert.setContentText("""
            Guest: %s %s
            Status: %s
            Balance: $%s
            """.formatted(
            selected.getGuest().getFirstName(),
            selected.getGuest().getLastName(),
            selected.getStatus(),
            outstanding(selected)
        ));
        alert.showAndWait();
    }

    @FXML
    public void cancelReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        reservationService.cancel(selected.getId());
        reloadAll();
    }

    @FXML
    public void checkoutReservation() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("No Selection", "Please select a reservation to checkout.", Alert.AlertType.WARNING);
            return;
        }
        try {
            reservationService.checkout(selected.getId());
            showAlert("Success", "Reservation checked out successfully!", Alert.AlertType.INFORMATION);
            reloadAll();
        } catch (IllegalStateException e) {
            showAlert("Checkout Failed", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Error", "Checkout failed: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    public void recordPayment() {
        Reservation selected = reservationTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            return;
        }
        Dialog<PaymentMethod> dialog = new Dialog<>();
        dialog.setTitle("Record Payment");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        ChoiceBox<PaymentMethod> methodChoice = new ChoiceBox<>(FXCollections.observableArrayList(PaymentMethod.values()));
        methodChoice.getSelectionModel().select(PaymentMethod.CASH);
        TextField amountField = new TextField(outstanding(selected));
        grid.addRow(0, methodChoice, amountField);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> button == ButtonType.OK ? methodChoice.getValue() : null);
        dialog.showAndWait().ifPresent(method -> {
            PaymentCommand command = new PaymentCommand(
                selected.getId(),
                method,
                new BigDecimal(amountField.getText()),
                false,
                authenticationService.currentAdmin().getEmail(),
                "front-desk"
            );
            paymentService.process(command);
            reloadAll();
        });
    }

    @FXML
    public void convertWaitlist() {
        WaitlistEntry entry = waitlistTable.getSelectionModel().getSelectedItem();
        if (entry == null) {
            return;
        }
        reservationService.createReservation(new ReservationRequest(
            entry.getGuestName(),
            "",
            entry.getEmail(),
            entry.getPhone(),
            2,
            0,
            entry.getStartDate(),
            entry.getEndDate(),
            Map.of(entry.getDesiredRoomType(), 1),
            List.of(),
            false,
            entry.getNotes()
        ));
        waitlistService.markConverted(entry.getId());
        reloadAll();
    }

    @FXML
    public void notifyWaitlist() {
        waitlistService.handleAvailabilityChange(
            com.blueharbor.hotel.observer.ReservationEvent.of(
                com.blueharbor.hotel.observer.ReservationEventType.AVAILABILITY_CHANGED,
                UUID.randomUUID(),
                "Manual notify"
            )
        );
        loadWaitlist();
    }

    @FXML
    public void addWaitlistEntry() {
        Dialog<WaitlistRequest> dialog = new Dialog<>();
        dialog.setTitle("Add Waitlist Entry");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane grid = new GridPane();
        TextField guestField = new TextField();
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        ChoiceBox<RoomType> typeChoice = new ChoiceBox<>(FXCollections.observableArrayList(RoomType.values()));
        typeChoice.getSelectionModel().select(RoomType.SINGLE);
        DatePicker start = new DatePicker(LocalDate.now().plusDays(1));
        DatePicker end = new DatePicker(LocalDate.now().plusDays(2));
        TextField notes = new TextField();
        grid.addRow(0, guestField, phoneField);
        grid.addRow(1, emailField, typeChoice);
        grid.addRow(2, start, end);
        grid.addRow(3, notes);
        dialog.getDialogPane().setContent(grid);
        dialog.setResultConverter(button -> {
            if (button == ButtonType.OK) {
                return new WaitlistRequest(
                    guestField.getText(),
                    phoneField.getText(),
                    emailField.getText(),
                    typeChoice.getValue(),
                    start.getValue(),
                    end.getValue(),
                    notes.getText()
                );
            }
            return null;
        });
        dialog.showAndWait().ifPresent(waitlistService::addToWaitlist);
        loadWaitlist();
    }

    @FXML
    public void exportDailyReport() {
        // Export daily revenue and occupancy summary
        loadReports();
        Path revenueFile = exportService.exportRevenuePdf(revenueItems);
        Path occupancyFile = exportService.exportOccupancyPdf(occupancyItems);
        toast("Daily report exported:\n" + revenueFile.toAbsolutePath() + "\n" + occupancyFile.toAbsolutePath());
    }

    @FXML
    public void exportFeedbackCsv() {
        Path file = exportService.exportFeedbackCsv(feedbackItems);
        toast("Feedback CSV saved to " + file.toAbsolutePath());
    }

    @FXML
    public void exportRevenueCsv() {
        Path file = exportService.exportRevenueCsv(revenueItems);
        toast("Revenue CSV saved to " + file.toAbsolutePath());
    }

    @FXML
    public void exportRevenuePdf() {
        Path file = exportService.exportRevenuePdf(revenueItems);
        toast("Revenue PDF saved to " + file.toAbsolutePath());
    }

    @FXML
    public void exportOccupancyCsv() {
        Path file = exportService.exportOccupancyCsv(occupancyItems);
        toast("Occupancy CSV saved to " + file.toAbsolutePath());
    }

    @FXML
    public void exportOccupancyPdf() {
        Path file = exportService.exportOccupancyPdf(occupancyItems);
        toast("Occupancy PDF saved to " + file.toAbsolutePath());
    }

    @FXML
    public void exportActivityCsv() {
        Path file = exportService.exportActivityCsv(activityItems);
        toast("Activity CSV saved to " + file.toAbsolutePath());
    }

    @FXML
    public void exportActivityTxt() {
        Path file = exportService.exportActivityTxt(activityItems);
        toast("Activity TXT saved to " + file.toAbsolutePath());
    }

    private void loadPayments() {
        paymentItems.setAll(paymentService.listPayments());
    }

    private void loadWaitlist() {
        waitlistItems.setAll(waitlistService.list(null));
    }

    private void loadFeedback() {
        List<Feedback> all = feedbackService.listAll();
        feedbackItems.setAll(all.stream()
            .filter(entry -> filterByRating(entry, ratingFilter.getValue()))
            .filter(entry -> filterByDate(entry, feedbackFromDate.getValue(), feedbackToDate.getValue()))
            .toList());
    }

    private boolean filterByRating(Feedback feedback, String filter) {
        if (filter == null || filter.equals("All")) {
            return true;
        }
        return feedback.getRating() >= Integer.parseInt(filter);
    }

    private boolean filterByDate(Feedback feedback, LocalDate from, LocalDate to) {
        LocalDate date = feedback.getSubmittedAt().toLocalDate();
        boolean after = from == null || !date.isBefore(from);
        boolean before = to == null || !date.isAfter(to);
        return after && before;
    }

    private void loadReports() {
        revenueItems.setAll(reportingService.revenue(new RevenueReportFilter(
            LocalDate.now().minusMonths(1),
            LocalDate.now(),
            "week"
        )));
        occupancyItems.setAll(reportingService.occupancy(new OccupancyReportFilter(
            LocalDate.now(),
            LocalDate.now().plusDays(7),
            "day"
        )));
    }

    private void loadActivity() {
        activityItems.setAll(reportingService.activity(new ActivityLogFilter(
            LocalDateTime.now().minusDays(7),
            LocalDateTime.now()
        )));
    }

    @FXML
    public void logout() {
        authenticationService.logout();
        navigator.goTo(ViewKey.LOGIN);
    }

    private SimpleStringProperty fx(String value) {
        return new SimpleStringProperty(value);
    }

    private String outstanding(Reservation reservation) {
        return outstandingValue(reservation).setScale(2, RoundingMode.HALF_UP).toString();
    }

    private BigDecimal outstandingValue(Reservation reservation) {
        BigDecimal paid = reservation.getPayments().stream()
            .map(payment -> payment.isRefund() ? payment.getAmount().negate() : payment.getAmount())
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return reservation.getTotal().subtract(paid);
    }

    private String blank(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private void toast(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type, content, ButtonType.OK);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.showAndWait();
    }
}
