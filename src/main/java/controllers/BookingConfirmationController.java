package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import dao.AppointmentDao;
import dao.MasterDao;
import dao.ServiceDao;
import models.Appointment;
import models.Master;
import models.Service;
import utils.CurrentAppointment;
import utils.UserSession;

public class BookingConfirmationController {

    @FXML private Label masterNameLabel;
    @FXML private Label serviceNameLabel;
    @FXML private Label dateLabel;
    @FXML private Label timeLabel;
    @FXML private Label priceLabel;
    @FXML private Label errorLabel;

    private AppointmentDao appointmentDao;
    private MasterDao masterDao;
    private ServiceDao serviceDao;

    private int masterId;
    private int serviceId;
    private int userId;
    private String selectedDate;
    private String selectedTime;
    private double price;

    @FXML
    public void initialize() {
        appointmentDao = new AppointmentDao();
        masterDao = new MasterDao();
        serviceDao = new ServiceDao();

        // Получаем данные из CurrentAppointment (новый класс-хранилище)
        Master currentMaster = CurrentAppointment.getMaster();
        Service currentService = CurrentAppointment.getService();
        selectedDate = CurrentAppointment.getSelectedDate();
        selectedTime = CurrentAppointment.getSelectedTime();

        if (currentMaster != null) {
            masterId = currentMaster.getId();
        } else {
            showError("Мастер не выбран");
            return;
        }
        if (currentService != null) {
            serviceId = currentService.getId();
        } else {
            showError("Услуга не выбрана");
            return;
        }

        // ID пользователя из сессии
        userId = UserSession.getUserId();
        if (userId <= 0) {
            showError("Пользователь не авторизован");
            return;
        }

        // Загружаем и отображаем информацию
        loadBookingInfo();
    }

    private void loadBookingInfo() {
        // Получаем информацию о мастере (можно из объекта CurrentAppointment, но для уверенности из БД)
        Master master = masterDao.findById(masterId);
        if (master != null) {
            masterNameLabel.setText("Мастер: " + master.getName());
        } else {
            masterNameLabel.setText("Мастер: не найден");
        }

        // Получаем информацию об услуге
        Service service = serviceDao.findById(serviceId);
        if (service != null) {
            serviceNameLabel.setText("Услуга: " + service.getName());
            price = service.getPrice();
            priceLabel.setText("Цена: " + price + " ₽");
        } else {
            serviceNameLabel.setText("Услуга: не найдена");
            priceLabel.setText("Цена: -");
        }

        // Отображаем дату и время
        dateLabel.setText("Дата: " + (selectedDate != null ? selectedDate : "не выбрана"));
        timeLabel.setText("Время: " + (selectedTime != null ? selectedTime : "не выбрано"));
    }

    @FXML
    private void confirmBooking(ActionEvent event) {
        // Проверяем, все ли данные есть
        if (selectedDate == null || selectedTime == null) {
            showError("Дата или время не выбраны");
            return;
        }

        if (userId <= 0) {
            showError("Пользователь не авторизован");
            return;
        }

        // Проверяем, свободен ли еще слот (на случай, если кто-то успел записаться)
        if (!appointmentDao.isSlotAvailable(masterId, selectedDate, selectedTime)) {
            showError("Это время уже занято! Пожалуйста, выберите другое.");
            return;
        }

        // Создаем запись
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setMasterId(masterId);
        appointment.setServiceId(serviceId);
        appointment.setDate(selectedDate);
        appointment.setTime(selectedTime);
        appointment.setStatus("pending");
        appointment.setFinalPrice(price);   // пока без скидки, можно потом добавить

        boolean success = appointmentDao.createAppointment(appointment); // используем метод createAppointment

        if (success) {
            // Очищаем выбранные данные
            CurrentAppointment.clear();

            // Переходим в окно "Мои записи"
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/client/my-bookings-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Ошибка перехода: " + e.getMessage());
            }
        } else {
            showError("Ошибка при создании записи. Попробуйте еще раз.");
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/client/calendar-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void cancelBooking(ActionEvent event) throws Exception {
        // Возвращаемся на главное окно клиента
        Parent root = FXMLLoader.load(getClass().getResource("/client/main-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}