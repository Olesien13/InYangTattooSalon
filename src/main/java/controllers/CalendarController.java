package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dao.AppointmentDao;
import dao.MasterScheduleDao;
import dao.ServiceDao;
import models.Master;
import models.Service;
import utils.CurrentAppointment;
import javafx.scene.control.DateCell;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CalendarController {

    @FXML private DatePicker datePicker;
    @FXML private VBox timeSlotsContainer;
    @FXML private Label errorLabel;
    @FXML private Label masterNameLabel;
    @FXML private Label serviceNameLabel;
    @FXML private Label priceLabel;

    private AppointmentDao appointmentDao;
    private MasterScheduleDao scheduleDao;
    private ServiceDao serviceDao;

    private int masterId;
    private int serviceId;
    private String selectedDate;
    private String selectedTime;

    @FXML
    public void initialize() {
        appointmentDao = new AppointmentDao();
        scheduleDao = new MasterScheduleDao();
        serviceDao = new ServiceDao();

        // получаем выбранного мастера и услугу из глобального хранилища
        Master currentMaster = CurrentAppointment.getMaster();
        Service currentService = CurrentAppointment.getService();

        if (currentMaster == null || currentService == null) {
            showError("Ошибка: не выбран мастер или услуга");
            return;
        }

        masterId = currentMaster.getId();
        serviceId = currentService.getId();

        // отображаем информацию о мастере и услуге
        loadServiceInfo();

        // настройка DatePicker: запрещаем выбор прошедших дат
        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                if (date != null && date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        // при выборе даты загружаем слоты
        datePicker.setOnAction(event -> loadTimeSlots());

        // загружаем слоты для текущей даты
        loadTimeSlots();
    }

    private void loadServiceInfo() {
        Service service = serviceDao.findById(serviceId);
        if (service != null) {
            serviceNameLabel.setText("Услуга: " + service.getName());
            priceLabel.setText("Цена: " + service.getPrice() + " ₽");
        }
        Master master = CurrentAppointment.getMaster();
        if (master != null) {
            masterNameLabel.setText("Мастер: " + master.getName());
        }
    }

    private void loadTimeSlots() {
        if (datePicker.getValue() == null) return;

        selectedDate = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
        timeSlotsContainer.getChildren().clear();

        int dayOfWeek = datePicker.getValue().getDayOfWeek().getValue();

        // получаем рабочие часы мастера
        String[] hours = scheduleDao.getWorkingHours(masterId, dayOfWeek);

        if (hours == null) {
            Label label = new Label("В этот день мастер не работает");
            label.setStyle("-fx-text-fill: red;");
            timeSlotsContainer.getChildren().add(label);
            return;
        }

        // генерируем слоты с интервалом 1 час (начало и конец из расписания)
        String[] startParts = hours[0].split(":");
        String[] endParts = hours[1].split(":");
        int startHour = Integer.parseInt(startParts[0]);
        int endHour = Integer.parseInt(endParts[0]);

        List<String> slots = new ArrayList<>();
        for (int hour = startHour; hour < endHour; hour++) {
            String timeSlot = String.format("%02d:00", hour);
            slots.add(timeSlot);
        }

        // создаём кнопки для каждого слота
        for (String slot : slots) {
            Button timeButton = new Button(slot);
            timeButton.setMaxWidth(Double.MAX_VALUE);
            timeButton.setStyle("-fx-font-size: 14px; -fx-padding: 8;");

            // проверяем, свободен ли слот
            if (appointmentDao.isSlotAvailable(masterId, selectedDate, slot)) {
                // при нажатии на свободный слот переходим к подтверждению
                timeButton.setOnAction(event -> selectTime(slot, event));
            } else {
                timeButton.setDisable(true);
                timeButton.setStyle("-fx-opacity: 0.5; -fx-background-color: #ff8888;");
                timeButton.setText(slot + " (занято)");
            }

            timeSlotsContainer.getChildren().add(timeButton);
        }

        if (slots.isEmpty()) {
            Label label = new Label("Нет свободных слотов в этот день");
            label.setStyle("-fx-text-fill: orange;");
            timeSlotsContainer.getChildren().add(label);
        }
    }

    // выбор времени и переход на окно подтверждения записи
    private void selectTime(String time, ActionEvent event) {
        selectedTime = time;

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/client/booking-confirmation.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка перехода: " + e.getMessage());
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/client/master-selection-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}