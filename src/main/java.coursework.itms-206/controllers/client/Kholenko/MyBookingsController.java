package controllers.client.Kholenko;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import dao.AppointmentDao;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Appointment;
import utils.CurrentAppointment;
import utils.UserSession;
import dao.MasterDao;
import dao.ServiceDao;

import java.time.LocalDate;
import java.util.List;

public class MyBookingsController {

    @FXML private TableView<Appointment> bookingsTable;
    @FXML private TableColumn<Appointment, String> dateColumn;
    @FXML private TableColumn<Appointment, String> timeColumn;
    @FXML private TableColumn<Appointment, String> masterColumn;
    @FXML private TableColumn<Appointment, String> serviceColumn;
    @FXML private TableColumn<Appointment, String> statusColumn;
    @FXML private Label selectedInfoLabel;
    @FXML private Button cancelButton;

    @FXML private ToggleButton allFilterBtn;
    @FXML private ToggleButton upcomingFilterBtn;
    @FXML private ToggleButton completedFilterBtn;

    private AppointmentDao appointmentDao;
    private ObservableList<Appointment> allAppointments = FXCollections.observableArrayList();
    private FilteredList<Appointment> filteredAppointments;
    private int rescheduleAppointmentId;
    private MasterDao masterDao;
    private ServiceDao serviceDao;

    @FXML
    public void initialize() {
        appointmentDao = new AppointmentDao();
        masterDao = new MasterDao();
        serviceDao = new ServiceDao();

        // Настройка колонок таблицы
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDate()));
        timeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTime()));
        masterColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMasterName()));
        serviceColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getServiceName()));
        statusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getStatus()));

        // Загрузка записей
        loadAppointments();

        // Создаём фильтрованный список на основе загруженных данных
        filteredAppointments = new FilteredList<>(allAppointments, p -> true);
        bookingsTable.setItems(filteredAppointments);

        // Обработчики фильтров
        allFilterBtn.setOnAction(e -> applyFilter("all"));
        upcomingFilterBtn.setOnAction(e -> applyFilter("upcoming"));
        completedFilterBtn.setOnAction(e -> applyFilter("completed"));

        // Выделение строки
        bookingsTable.getSelectionModel().selectedItemProperty().addListener((obs, old, selected) -> {
            if (selected != null) {
                selectedInfoLabel.setText("Выбрана запись: " + selected.getDate() + " " + selected.getTime() + ", " +
                        selected.getMasterName() + ", " + selected.getServiceName());
                boolean canCancel = (selected.getStatus().equals("pending") || selected.getStatus().equals("confirmed"))
                        && LocalDate.parse(selected.getDate()).isAfter(LocalDate.now());
                cancelButton.setDisable(!canCancel);
            } else {
                selectedInfoLabel.setText("Выбрана запись: —");
                cancelButton.setDisable(true);
            }
        });

        // Отображение статуса на русском
        statusColumn.setCellFactory(column -> new TableCell<Appointment, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else {
                    switch (item) {
                        case "pending": setText("Ожидает"); break;
                        case "confirmed": setText("Подтверждена"); break;
                        case "completed": setText("Выполнена"); break;
                        case "cancelled": setText("Отменена"); break;
                        default: setText(item);
                    }
                }
            }
        });
    }

    private void loadAppointments() {
        int userId = UserSession.getUserId();
        if (userId <= 0) return;
        List<Appointment> list = appointmentDao.getByUserId(userId);
        allAppointments.setAll(list);
    }

    private void applyFilter(String filter) {
        if (filteredAppointments == null) return;
        switch (filter) {
            case "upcoming":
                filteredAppointments.setPredicate(app -> {
                    String status = app.getStatus();
                    LocalDate date = LocalDate.parse(app.getDate());
                    return (status.equals("pending") || status.equals("confirmed")) && date.isAfter(LocalDate.now());
                });
                upcomingFilterBtn.setSelected(true);
                allFilterBtn.setSelected(false);
                completedFilterBtn.setSelected(false);
                break;
            case "completed":
                filteredAppointments.setPredicate(app -> {
                    String status = app.getStatus();
                    LocalDate date = LocalDate.parse(app.getDate());
                    return status.equals("completed") || status.equals("cancelled") || date.isBefore(LocalDate.now());
                });
                completedFilterBtn.setSelected(true);
                allFilterBtn.setSelected(false);
                upcomingFilterBtn.setSelected(false);
                break;
            default: // "all"
                filteredAppointments.setPredicate(app -> true);
                allFilterBtn.setSelected(true);
                upcomingFilterBtn.setSelected(false);
                completedFilterBtn.setSelected(false);
                break;
        }
    }

    @FXML
    private void rescheduleBooking() {
        Appointment selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        rescheduleAppointmentId = selected.getId();
        CurrentAppointment.setMaster(masterDao.findById(selected.getMasterId()));
        CurrentAppointment.setService(serviceDao.findById(selected.getServiceId()));
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client.Kholenko/calendar-view.fxml"));
            Parent root = loader.load();
            CalendarController calendarController = loader.getController();
            calendarController.setRescheduleMode(true, rescheduleAppointmentId);
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadAppointments();
            applyFilter(getCurrentFilter());
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка открытия календаря");
        }
    }

    @FXML
    private void cancelBooking() {
        Appointment selected = bookingsTable.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Отмена записи");
        confirm.setContentText("Вы уверены, что хотите отменить запись на " + selected.getDate() + " " + selected.getTime() + "?");
        if (confirm.showAndWait().orElse(ButtonType.NO) == ButtonType.YES) {
            boolean updated = appointmentDao.updateStatus(selected.getId(), "cancelled");
            if (updated) {
                loadAppointments();
                applyFilter(getCurrentFilter());
                showInfo("Запись отменена");
            } else {
                showError("Ошибка при отмене");
            }
        }
    }

    private String getCurrentFilter() {
        if (upcomingFilterBtn.isSelected()) return "upcoming";
        if (completedFilterBtn.isSelected()) return "completed";
        return "all";
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
        alert.showAndWait();
    }
}