package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dao.AppointmentDao;
import dao.MasterScheduleDao;
import dao.ServiceDao;
import models.Service;
import utils.UserSession;
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

        // Получаем ID из сессии
        masterId = UserSession.getSelectedMasterId();
        serviceId = UserSession.getSelectedServiceId();

        // Показываем информацию
        loadServiceInfo();

        // Настройка DatePicker
        datePicker.setValue(LocalDate.now());
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {  // ← ИСПРАВЛЕНО
                super.updateItem(date, empty);
                if (date != null && date.isBefore(LocalDate.now())) {
                    setDisable(true);
                }
            }
        });

        // Обработчик выбора даты
        datePicker.setOnAction(event -> loadTimeSlots());

        // Загружаем слоты для сегодняшней даты
        loadTimeSlots();
    }

    private void loadServiceInfo() {
        Service service = serviceDao.findById(serviceId);
        if (service != null) {
            serviceNameLabel.setText("Услуга: " + service.getName());
            priceLabel.setText("Цена: " + service.getPrice() + " ₽");
        }
        masterNameLabel.setText("Мастер: ID " + masterId);
    }

    private void loadTimeSlots() {
        if (datePicker.getValue() == null) return;

        selectedDate = datePicker.getValue().format(DateTimeFormatter.ISO_LOCAL_DATE);
        timeSlotsContainer.getChildren().clear();

        // Определяем день недели (1=пн ... 7=вс)
        int dayOfWeek = datePicker.getValue().getDayOfWeek().getValue();
        // Конвертируем: в понедельник = 1, в БД тоже 1
        if (dayOfWeek == 7) dayOfWeek = 7; // воскресенье

        // Получаем рабочие часы мастера
        String[] hours = scheduleDao.getWorkingHours(masterId, dayOfWeek);

        if (hours == null) {
            Label label = new Label("В этот день мастер не работает");
            label.setStyle("-fx-text-fill: red;");
            timeSlotsContainer.getChildren().add(label);
            return;
        }

        // Генерируем слоты с интервалом 1 час
        String[] startParts = hours[0].split(":");
        String[] endParts = hours[1].split(":");

        int startHour = Integer.parseInt(startParts[0]);
        int endHour = Integer.parseInt(endParts[0]);

        List<String> slots = new ArrayList<>();
        for (int hour = startHour; hour < endHour; hour++) {
            String timeSlot = String.format("%02d:00", hour);
            slots.add(timeSlot);
        }

        // Показываем слоты
        for (String slot : slots) {
            Button timeButton = new Button(slot);
            timeButton.setMaxWidth(Double.MAX_VALUE);
            timeButton.setStyle("-fx-font-size: 14px; -fx-padding: 8;");

            // Проверяем, свободен ли слот
            if (appointmentDao.isSlotAvailable(masterId, selectedDate, slot)) {
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

    private void selectTime(String time, ActionEvent event) {
        selectedTime = time;
        UserSession.setSelectedDate(selectedDate);
        UserSession.setSelectedTime(selectedTime);

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
        Parent root = FXMLLoader.load(getClass().getResource("/client/master-selection.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // Геттеры для выбранной даты и времени
    //public static String getSelectedDate() {
    //    return selectedDate;
    //}

    //public static String getSelectedTime() {
    //    return selectedTime;
    //}

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}