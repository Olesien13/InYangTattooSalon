package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.MasterDao;
import dao.MasterScheduleDao;
import models.Master;
import models.MasterSchedule;
import utils.UserSession;

import java.util.List;

public class AddScheduleController {

    @FXML private ComboBox<Master> masterComboBox;
    @FXML private ComboBox<String> dayComboBox;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private Label errorLabel;

    private MasterScheduleDao scheduleDao;
    private MasterDao masterDao;

    @FXML
    public void initialize() {
        scheduleDao = new MasterScheduleDao();
        masterDao = new MasterDao();

        // Загружаем список мастеров
        List<Master> masters = masterDao.getAll();
        masterComboBox.getItems().setAll(masters);
        masterComboBox.setPromptText("Выберите мастера");

        // Настройка отображения имени мастера в ComboBox
        masterComboBox.setCellFactory(lv -> new ListCell<Master>() {
            @Override
            protected void updateItem(Master master, boolean empty) {
                super.updateItem(master, empty);
                setText(empty ? null : master.getName());
            }
        });
        masterComboBox.setButtonCell(new ListCell<Master>() {
            @Override
            protected void updateItem(Master master, boolean empty) {
                super.updateItem(master, empty);
                setText(empty ? null : master.getName());
            }
        });

        // Загружаем дни недели
        dayComboBox.getItems().addAll(
                "Понедельник", "Вторник", "Среда",
                "Четверг", "Пятница", "Суббота", "Воскресенье"
        );
        dayComboBox.setPromptText("Выберите день недели");
    }

    @FXML
    private void saveSchedule() {
        // Проверка выбора мастера
        Master selectedMaster = masterComboBox.getValue();
        if (selectedMaster == null) {
            showError("Выберите мастера");
            return;
        }

        // Проверка выбора дня
        String selectedDay = dayComboBox.getValue();
        if (selectedDay == null) {
            showError("Выберите день недели");
            return;
        }

        // Проверка времени начала
        String startTime = startTimeField.getText().trim();
        if (startTime.isEmpty()) {
            showError("Введите время начала работы");
            return;
        }

        // Проверка времени окончания
        String endTime = endTimeField.getText().trim();
        if (endTime.isEmpty()) {
            showError("Введите время окончания работы");
            return;
        }

        // Преобразование дня недели в число
        int dayNumber = convertDayToNumber(selectedDay);

        // Создание записи расписания
        MasterSchedule schedule = new MasterSchedule();
        schedule.setMasterId(selectedMaster.getId());
        schedule.setDayOfWeek(dayNumber);
        schedule.setStartTime(startTime);
        schedule.setEndTime(endTime);

        boolean created = scheduleDao.create(schedule);

        if (created) {
            closeWindow();
        } else {
            showError("Ошибка при сохранении расписания");
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) startTimeField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }

    private int convertDayToNumber(String dayName) {
        switch (dayName) {
            case "Понедельник": return 1;
            case "Вторник": return 2;
            case "Среда": return 3;
            case "Четверг": return 4;
            case "Пятница": return 5;
            case "Суббота": return 6;
            case "Воскресенье": return 7;
            default: return 1;
        }
    }
}