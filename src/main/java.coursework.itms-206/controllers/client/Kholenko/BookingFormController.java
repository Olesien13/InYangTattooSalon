package controllers.client.Kholenko;

import dao.UserDao;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import dao.AppointmentDao;
import dao.ReferenceImageDao;
import models.Appointment;
import models.User;
import utils.CurrentAppointment;
import utils.UserSession;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

// контроллер для формы записи
public class BookingFormController {

    // поля для элементов ввода из fxml
    @FXML private TextField fullNameField;      // поле фио
    @FXML private TextField phoneField;         // поле телефона
    @FXML private TextField emailField;         // поле email
    @FXML private ComboBox<String> sizeComboBox; // выпадающий список для размера тату
    @FXML private Label errorLabel;             // метка для ошибок
    @FXML private Button uploadButton;          // кнопка выбора файлов
    @FXML private Button submitButton;          // кнопка "записаться"

    @FXML private Label infoLabel;              // метка с информацией о выбранных услуге, мастере, дате и времени

    // dao для работы с записями и референсами
    private AppointmentDao appointmentDao;
    private ReferenceImageDao referenceImageDao;

    // список выбранных файлов (референсов)
    private List<File> selectedFiles = new ArrayList<>();

    // метод инициализации контроллера
    @FXML
    public void initialize() {
        appointmentDao = new AppointmentDao();
        referenceImageDao = new ReferenceImageDao();

        // заполняем информационную метку данными из CurrentAppointment
        if (CurrentAppointment.getService() != null && CurrentAppointment.getMaster() != null
                && CurrentAppointment.getSelectedDate() != null && CurrentAppointment.getSelectedTime() != null) {
            infoLabel.setText(String.format("%s | %s | %s | %s",
                    CurrentAppointment.getService().getName(),
                    CurrentAppointment.getMaster().getName(),
                    CurrentAppointment.getSelectedDate(),
                    CurrentAppointment.getSelectedTime()
            ));
        } else {
            infoLabel.setText("Данные о записи не выбраны");
        }

        // заполняем выпадающий список вариантами размера тату
        sizeComboBox.getItems().addAll("Маленькая", "Средняя", "Большая");
        sizeComboBox.setValue("Средняя"); // значение по умолчанию

        // обработчик кнопки выбора файлов
        uploadButton.setOnAction(e -> chooseFiles());

        // загружаем данные из профиля текущего пользователя для автозаполнения полей
        int userId = UserSession.getUserId();
        if (userId > 0) {
            UserDao userDao = new UserDao();
            User user = userDao.findById(userId);
            if (user != null) {
                String fullName = (user.getLastName() != null ? user.getLastName() : "") + " " +
                        (user.getFirstName() != null ? user.getFirstName() : "") + " " +
                        (user.getMiddleName() != null ? user.getMiddleName() : "");
                fullNameField.setText(fullName.trim());
                phoneField.setText(user.getPhone() != null ? user.getPhone() : "");
                emailField.setText(user.getEmail() != null ? user.getEmail() : "");
            }
        }
    }

    // открывает диалог выбора файлов (поддерживается выбор нескольких изображений)
    private void chooseFiles() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите референсы (эскизы)");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Изображения", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        List<File> files = fileChooser.showOpenMultipleDialog(uploadButton.getScene().getWindow());
        if (files != null && !files.isEmpty()) {
            selectedFiles.addAll(files);
            uploadButton.setText("Выбрано файлов: " + selectedFiles.size());
        }
    }

    // обработчик кнопки "записаться"
    @FXML
    private void submitBooking() {

        // валидация полей
        String fullName = fullNameField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        String size = sizeComboBox.getValue();

        if (fullName.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            showError("Заполните все обязательные поля (ФИО, телефон, email).");
            return;
        }
        if (size == null) {
            showError("Выберите размер тату.");
            return;
        }

        // получаем данные из хранилища CurrentAppointment
        int userId = UserSession.getUserId();
        if (userId <= 0) {
            showError("Пользователь не авторизован.");
            return;
        }
        int masterId = CurrentAppointment.getMaster().getId();
        int serviceId = CurrentAppointment.getService().getId();
        String date = CurrentAppointment.getSelectedDate();
        String time = CurrentAppointment.getSelectedTime();

        if (date == null || time == null) {
            showError("Дата или время не выбраны.");
            return;
        }

        // создаем объект записи
        Appointment appointment = new Appointment();
        appointment.setUserId(userId);
        appointment.setMasterId(masterId);
        appointment.setServiceId(serviceId);
        appointment.setDate(date);
        appointment.setTime(time);
        appointment.setStatus("pending");
        appointment.setSize(size);
        appointment.setFinalPrice(CurrentAppointment.getService().getPrice()); // цена без скидки

        // сохраняем запись и получаем сгенерированный id
        int appointmentId = appointmentDao.createAndGetId(appointment);
        if (appointmentId <= 0) {
            showError("Ошибка при создании записи. Попробуйте еще раз.");
            return;
        }

        // сохраняем загруженные референсы (копируем файлы и записываем пути в бд)
        saveReferenceImages(appointmentId);

        // очищаем временные данные сессии
        CurrentAppointment.clear();

        // переходим на главное окно и активируем вкладку "мои записи"
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client.Kholenko/main-view.fxml"));
            Parent root = loader.load();
            MainController mainController = loader.getController();
            mainController.setActiveTab("Мои записи");
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showError("Ошибка перехода: " + e.getMessage());
        }
    }

    // сохраняет выбранные референсы: копирует файлы в папку references/ и записывает пути в бд
    private void saveReferenceImages(int appointmentId) {
        if (selectedFiles.isEmpty()) return;

        // папка для хранения референсов (в корне проекта)
        String baseDir = System.getProperty("user.dir") + File.separator + "references";
        File dir = new File(baseDir);
        if (!dir.exists()) dir.mkdirs();

        for (File file : selectedFiles) {
            try {

                // генерируем уникальное имя файла (время + оригинальное имя)
                String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss_"));
                String fileName = timestamp + file.getName();
                Path destination = Paths.get(baseDir, fileName);

                // копируем файл
                Files.copy(file.toPath(), destination, StandardCopyOption.REPLACE_EXISTING);

                // сохраняем путь в бд
                String dbPath = "references/" + fileName;
                referenceImageDao.insert(appointmentId, dbPath, fileName);
            } catch (IOException e) {
                e.printStackTrace();
                // не прерываем сохранение остальных файлов при ошибке
            }
        }
        selectedFiles.clear();
    }

    // кнопка "назад" – возврат к календарю
    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/client.Kholenko/calendar-view.fxml"));
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // отображает сообщение об ошибке
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}