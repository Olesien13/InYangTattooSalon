package coursework.itms206.controllers.admin.Shokina;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import coursework.itms206.dao.MasterDao;
import coursework.itms206.dao.MasterScheduleDao;
import coursework.itms206.models.Master;
import coursework.itms206.models.MasterSchedule;
import java.util.List;

// контроллер окна добавления рабочего дня

public class AddScheduleController {

    @FXML private ComboBox<Master> masterComboBox;   // выбор мастера
    @FXML private ComboBox<String> dayComboBox;      // выбор дня недели
    @FXML private TextField startTimeField;          // время начала
    @FXML private TextField endTimeField;            // время окончания
    @FXML private Label errorLabel;                  // метка ошибок

    private MasterScheduleDao scheduleDao;   // dao для работы с расписанием
    private MasterDao masterDao;             // dao для работы с мастерами

    @FXML
    public void initialize() {
        scheduleDao = new MasterScheduleDao();
        masterDao = new MasterDao();

        // загрузка списка мастеров
        List<Master> masters = masterDao.getAll();
        masterComboBox.getItems().setAll(masters);
        masterComboBox.setPromptText("Выберите мастера");

        // отображение имени мастера
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

        // загрузка дней недели
        dayComboBox.getItems().addAll(
                "Понедельник", "Вторник", "Среда",
                "Четверг", "Пятница", "Суббота", "Воскресенье"
        );
        dayComboBox.setPromptText("Выберите день недели");
    }

    // сохранение нового рабочего дня
    @FXML
    private void saveSchedule() {
        Master selectedMaster = masterComboBox.getValue();
        if (selectedMaster == null) {
            showError("Выберите мастера");
            return;
        }

        String selectedDay = dayComboBox.getValue();
        if (selectedDay == null) {
            showError("Выберите день недели");
            return;
        }

        String startTime = startTimeField.getText().trim();
        if (startTime.isEmpty()) {
            showError("Введите время начала работы");
            return;
        }

        String endTime = endTimeField.getText().trim();
        if (endTime.isEmpty()) {
            showError("Введите время окончания работы");
            return;
        }

        int dayNumber = convertDayToNumber(selectedDay);

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

    // преобразование названия дня в число
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