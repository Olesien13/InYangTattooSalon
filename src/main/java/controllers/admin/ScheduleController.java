package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.MasterDao;
import dao.MasterScheduleDao;
import models.Master;
import models.MasterSchedule;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScheduleController {

    @FXML private TableView<MasterScheduleRow> scheduleTable;
    @FXML private TableColumn<MasterSchedule, String> colMaster;
    @FXML private TableColumn<MasterSchedule, String> colMon;
    @FXML private TableColumn<MasterSchedule, String> colTue;
    @FXML private TableColumn<MasterSchedule, String> colWed;
    @FXML private TableColumn<MasterSchedule, String> colThu;
    @FXML private TableColumn<MasterSchedule, String> colFri;
    @FXML private TableColumn<MasterSchedule, String> colSat;
    @FXML private TableColumn<MasterSchedule, String> colSun;

    @FXML private Button employeesMenuBtn;
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    private MasterDao masterDao;
    private MasterScheduleDao scheduleDao;

    // Класс для хранения расписания одного мастера
    public class MasterScheduleRow {
        private String masterName;
        private String mon;
        private String tue;
        private String wed;
        private String thu;
        private String fri;
        private String sat;
        private String sun;

        public MasterScheduleRow(String masterName) {
            this.masterName = masterName;
            this.mon = "";
            this.tue = "";
            this.wed = "";
            this.thu = "";
            this.fri = "";
            this.sat = "";
            this.sun = "";
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

        // Настройка колонок таблицы
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

    private void loadSchedule() {
        // Очищаем таблицу
        scheduleTable.getItems().clear();

        // Получаем всех мастеров
        List<Master> masters = masterDao.getAll();

        // Для каждого мастера создаём строку расписания
        for (Master master : masters) {
            MasterScheduleRow row = new MasterScheduleRow(master.getName());

            // Получаем расписание мастера
            List<MasterSchedule> schedules = scheduleDao.getByMasterId(master.getId());

            // Заполняем дни недели
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

            // Добавляем строку в таблицу
            scheduleTable.getItems().add(row);
        }
    }

    @FXML
    private void goToEmployees() {
        loadSchedule(); // Обновляем таблицу
    }

    @FXML
    private void addSchedule() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/add-schedule.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Добавить рабочий день");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadSchedule(); // Обновляем таблицу после закрытия
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно добавления");
        }
    }

    @FXML
    private void editSchedule() {
        MasterScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите строку для редактирования");
            return;
        }

        // Находим мастера по имени
        Master master = masterDao.getAll().stream()
                .filter(m -> m.getName().equals(selected.getMasterName()))
                .findFirst()
                .orElse(null);

        if (master == null) {
            showAlert("Ошибка", "Мастер не найден");
            return;
        }

        // Сохраняем ID мастера в сессию для окна редактирования
        utils.UserSession.setSelectedMasterId(master.getId());

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/change-schedule.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Редактировать расписание: " + master.getName());
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadSchedule(); // Обновляем таблицу после закрытия
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно редактирования");
        }
    }

    @FXML
    private void deleteSchedule() {
        MasterScheduleRow selected = scheduleTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите строку для удаления");
            return;
        }

        // Находим мастера по имени
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
            boolean deleted = scheduleDao.deleteByMasterId(master.getId());
            if (deleted) {
                showAlert("Успех", "Расписание удалено");
                loadSchedule();
            } else {
                showAlert("Ошибка", "Не удалось удалить расписание");
            }
        }
    }

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/admin/employees.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Сотрудники");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось вернуться назад");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}