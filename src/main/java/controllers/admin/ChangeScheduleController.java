package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.MasterScheduleDao;
import models.MasterSchedule;

public class ChangeScheduleController {

    @FXML private ComboBox<String> dayComboBox;
    @FXML private TextField startTimeField;
    @FXML private TextField endTimeField;
    @FXML private Label errorLabel;

    private MasterScheduleDao scheduleDao;
    private MasterSchedule currentSchedule;

    @FXML
    public void initialize() {
        scheduleDao = new MasterScheduleDao();

        dayComboBox.getItems().addAll(
                "Понедельник", "Вторник", "Среда",
                "Четверг", "Пятница", "Суббота", "Воскресенье"
        );
    }

    public void setSchedule(MasterSchedule schedule) {
        this.currentSchedule = schedule;

        dayComboBox.setValue(convertNumberToDay(schedule.getDayOfWeek()));
        startTimeField.setText(schedule.getStartTime());
        endTimeField.setText(schedule.getEndTime());
    }

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