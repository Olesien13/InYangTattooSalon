package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.MasterScheduleDao;
import models.MasterSchedule;

// контроллер окна редактирования расписания

public class ChangeScheduleController {

    @FXML private ComboBox<String> dayComboBox;      // выбор дня недели
    @FXML private TextField startTimeField;          // время начала
    @FXML private TextField endTimeField;            // время окончания
    @FXML private Label errorLabel;                  // метка ошибок

    private MasterScheduleDao scheduleDao;           // dao для работы с расписанием
    private MasterSchedule currentSchedule;          // редактируемая запись

    @FXML
    public void initialize() {
        scheduleDao = new MasterScheduleDao();

        // заполнение выпадающего списка днями недели
        dayComboBox.getItems().addAll(
                "Понедельник", "Вторник", "Среда",
                "Четверг", "Пятница", "Суббота", "Воскресенье"
        );
    }

    // передача данных из ScheduleController
    public void setSchedule(MasterSchedule schedule) {
        this.currentSchedule = schedule;

        dayComboBox.setValue(convertNumberToDay(schedule.getDayOfWeek()));
        startTimeField.setText(schedule.getStartTime());
        endTimeField.setText(schedule.getEndTime());
    }

    // сохранение изменений
    @FXML
    private void updateSchedule() {
        if (dayComboBox.getValue() == null) {
            showError("Выберите день недели");
            return;
        }
        if (startTimeField.getText().trim().isEmpty()) {
            showError("Введите время начала");
            return;
        }
        if (endTimeField.getText().trim().isEmpty()) {
            showError("Введите время окончания");
            return;
        }

        currentSchedule.setDayOfWeek(convertDayToNumber(dayComboBox.getValue()));
        currentSchedule.setStartTime(startTimeField.getText().trim());
        currentSchedule.setEndTime(endTimeField.getText().trim());

        boolean updated = scheduleDao.update(currentSchedule);

        if (updated) {
            closeWindow();
        } else {
            showError("Ошибка при обновлении");
        }
    }

    // отмена и закрытие
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

    // преобразование названия дня в число (1-7)
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

    // преобразование числа в название дня
    private String convertNumberToDay(int number) {
        switch (number) {
            case 1: return "Понедельник";
            case 2: return "Вторник";
            case 3: return "Среда";
            case 4: return "Четверг";
            case 5: return "Пятница";
            case 6: return "Суббота";
            case 7: return "Воскресенье";
            default: return "Понедельник";
        }
    }
}