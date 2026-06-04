package coursework.itms206.controllers.admin.Shokina;

import coursework.itms206.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import coursework.itms206.dao.MasterDao;
import coursework.itms206.dao.MasterScheduleDao;
import coursework.itms206.models.Master;
import coursework.itms206.models.MasterSchedule;
import java.io.IOException;
import java.util.List;

// контроллер окна расписания мастеров

public class ScheduleController {

    // таблица и колонки
    @FXML private TableView<MasterScheduleRow> scheduleTable;
    @FXML private TableColumn<MasterScheduleRow, String> colMaster;
    @FXML private TableColumn<MasterScheduleRow, String> colMon;
    @FXML private TableColumn<MasterScheduleRow, String> colTue;
    @FXML private TableColumn<MasterScheduleRow, String> colWed;
    @FXML private TableColumn<MasterScheduleRow, String> colThu;
    @FXML private TableColumn<MasterScheduleRow, String> colFri;
    @FXML private TableColumn<MasterScheduleRow, String> colSat;
    @FXML private TableColumn<MasterScheduleRow, String> colSun;

    // кнопки
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    private MasterDao masterDao;
    private MasterScheduleDao scheduleDao;

    // класс для хранения расписания одного мастера (строка таблицы)
    public static class MasterScheduleRow {
        private String masterName;
        private String mon, tue, wed, thu, fri, sat, sun;

        public MasterScheduleRow(String masterName) {
            this.masterName = masterName;
            mon = tue = wed = thu = fri = sat = sun = "";
        }

        public String getMasterName() { return masterName; }
        public String getMon() { return mon; }
        public void setMon(String mon) { this.mon = mon; }
        public String getTue() { return tue; }
        public void setTue(String tue) { this.tue = tue; }
        public String getWed() { return wed; }
        public void setWed(String wed) { this.wed = wed; }
        public String getThu() { return thu; }
        public void setThu(String thu) { this.thu = thu; }
        public String getFri() { return fri; }
        public void setFri(String fri) { this.fri = fri; }
        public String getSat() { return sat; }
        public void setSat(String sat) { this.sat = sat; }
        public String getSun() { return sun; }
        public void setSun(String sun) { this.sun = sun; }
    }

    @FXML
    public void initialize() {
        masterDao = new MasterDao();
        scheduleDao = new MasterScheduleDao();

        // настройка колонок таблицы
        colMaster.setCellValueFactory(new PropertyValueFactory<>("masterName"));
        colMon.setCellValueFactory(new PropertyValueFactory<>("mon"));
        colTue.setCellValueFactory(new PropertyValueFactory<>("tue"));
        colWed.setCellValueFactory(new PropertyValueFactory<>("wed"));
        colThu.setCellValueFactory(new PropertyValueFactory<>("thu"));
        colFri.setCellValueFactory(new PropertyValueFactory<>("fri"));
        colSat.setCellValueFactory(new PropertyValueFactory<>("sat"));
        colSun.setCellValueFactory(new PropertyValueFactory<>("sun"));

        loadSchedule();
    }

    // загрузка расписания всех мастеров
    private void loadSchedule() {
        scheduleTable.getItems().clear();

        List<Master> masters = masterDao.getAll();

        for (Master master : masters) {
            MasterScheduleRow row = new MasterScheduleRow(master.getName());
            List<MasterSchedule> schedules = scheduleDao.getByMasterId(master.getId());

            for (MasterSchedule schedule : schedules) {
                String time = schedule.getStartTime() + "-" + schedule.getEndTime();
                switch (schedule.getDayOfWeek()) {
                    case 1: row.setMon(time); break;
                    case 2: row.setTue(time); break;
                    case 3: row.setWed(time); break;
                    case 4: row.setThu(time); break;
                    case 5: row.setFri(time); break;
                    case 6: row.setSat(time); break;
                    case 7: row.setSun(time); break;
                }
            }
            scheduleTable.getItems().add(row);
        }
    }

    // открыть окно добавления рабочего дня
    @FXML
    private void addSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin.Shokina/add-schedule.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Добавить рабочий день");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadSchedule();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно добавления");
        }
    }

    // открыть окно редактирования расписания мастера
    @FXML
    private void editSchedule() {
        MasterScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите строку для редактирования");
            return;
        }

        Master master = masterDao.getAll().stream()
                .filter(m -> m.getName().equals(selected.getMasterName()))
                .findFirst()
                .orElse(null);

        if (master == null) {
            showAlert("Ошибка", "Мастер не найден");
            return;
        }

        UserSession.setSelectedMasterId(master.getId());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin.Shokina/change-schedule.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Редактировать расписание: " + master.getName());
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadSchedule();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно редактирования");
        }
    }

    // удалить всё расписание мастера
    @FXML
    private void deleteSchedule() {
        MasterScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите строку для удаления");
            return;
        }

        Master master = masterDao.getAll().stream()
                .filter(m -> m.getName().equals(selected.getMasterName()))
                .findFirst()
                .orElse(null);

        if (master == null) {
            showAlert("Ошибка", "Мастер не найден");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение удаления");
        confirm.setHeaderText("Удаление расписания");
        confirm.setContentText("Вы уверены, что хотите удалить всё расписание мастера " + master.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (scheduleDao.deleteByMasterId(master.getId())) {
                showAlert("Успех", "Расписание удалено");
                loadSchedule();
            } else {
                showAlert("Ошибка", "Не удалось удалить расписание");
            }
        }
    }

    // возврат в окно сотрудников
    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/admin.Shokina/employees.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Сотрудники");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось вернуться назад");
        }
    }

    // показать сообщение
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}