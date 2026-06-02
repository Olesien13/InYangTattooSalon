package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.MasterDao;
import models.Master;
import java.io.IOException;
import java.util.List;

// контроллер окна управления сотрудниками

public class EmployeesController {

    // таблица и колонки
    @FXML private TableView<Master> employeesTable;
    @FXML private TableColumn<Master, Integer> colId;
    @FXML private TableColumn<Master, String> colName;
    @FXML private TableColumn<Master, String> colPhone;
    @FXML private TableColumn<Master, String> colPosition;
    @FXML private TableColumn<Master, String> colHireDate;
    @FXML private TableColumn<Master, Double> colSalary;

    // кнопки действий
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;
    @FXML private Button scheduleButton;

    // кнопки меню (навигация)
    @FXML private Button employeesMenuBtn;
    @FXML private Button consumablesMenuBtn;
    @FXML private Button servicesMenuBtn;
    @FXML private Button clientsMenuBtn;

    private MasterDao masterDao;

    @FXML
    public void initialize() {
        masterDao = new MasterDao();

        // привязка колонок к полям модели Master
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colPosition.setCellValueFactory(new PropertyValueFactory<>("specialization"));
        colHireDate.setCellValueFactory(new PropertyValueFactory<>("hireDate"));
        colSalary.setCellValueFactory(new PropertyValueFactory<>("salary"));

        loadEmployees();   // загрузка списка сотрудников
    }

    // загрузка списка сотрудников из бд
    private void loadEmployees() {
        List<Master> masters = masterDao.getAll();
        employeesTable.getItems().setAll(masters);
    }

    // открыть окно добавления сотрудника
    @FXML
    private void addEmployee() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/add-employees.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Добавить сотрудника");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadEmployees();   // обновить таблицу
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно добавления");
        }
    }

    // открыть окно редактирования выбранного сотрудника
    @FXML
    private void editEmployee() {
        Master selected = employeesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите сотрудника для редактирования");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/change-employees.fxml"));
            Parent root = loader.load();

            ChangeEmployeeController controller = loader.getController();
            controller.setMaster(selected);

            Stage stage = new Stage();
            stage.setTitle("Редактировать сотрудника");
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadEmployees();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно редактирования");
        }
    }

    // удалить выбранного сотрудника
    @FXML
    private void deleteEmployee() {
        Master selected = employeesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите сотрудника для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение удаления");
        confirm.setHeaderText("Удаление сотрудника");
        confirm.setContentText("Вы уверены, что хотите удалить " + selected.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            if (masterDao.delete(selected.getId())) {
                showAlert("Успех", "Сотрудник успешно удалён");
                loadEmployees();
            } else {
                showAlert("Ошибка", "Не удалось удалить сотрудника");
            }
        }
    }

    // открыть окно расписания мастеров
    @FXML
    private void showSchedule() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/admin/schedule.fxml"));
            Stage stage = (Stage) scheduleButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Расписание мастеров");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть расписание");
        }
    }
    // навигация по меню
    @FXML
    private void goToEmployees() {
        // уже на этой странице
    }

    @FXML
    private void goToConsumables() {
        openWindow("/admin/consumables.fxml", "Расходники");
    }

    @FXML
    private void goToServices() {
        openWindow("/admin/service.fxml", "Услуги");
    }

    @FXML
    private void goToClients() {
        openWindow("/admin/clientele.fxml", "Клиенты");
    }

    @FXML
    private void goBack() {
        openWindow("/admin/menu.fxml", "Главное меню");
    }

    // универсальный метод для переключения окон
    private void openWindow(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) employeesTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно: " + title);
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