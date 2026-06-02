package controllers.admin.Shokina;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.ServiceDao;
import models.Service;
import java.io.IOException;

// контроллер окна управления услугами

public class ServicesController {

    @FXML private TableView<Service> servicesTable;           // таблица услуг
    @FXML private TableColumn<Service, Integer> colId;        // колонка id
    @FXML private TableColumn<Service, String> colName;       // колонка название
    @FXML private TableColumn<Service, Integer> colDuration;  // колонка время выполнения
    @FXML private TableColumn<Service, Double> colPrice;      // колонка цена
    @FXML private TableColumn<Service, String> colMaster;     // колонка мастер

    private ServiceDao serviceDao;   // dao для работы с услугами

    @FXML
    public void initialize() {
        serviceDao = new ServiceDao();

        // привязка колонок к полям модели Service
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colMaster.setCellValueFactory(new PropertyValueFactory<>("masterName"));

        loadServices();   // загрузка списка услуг
    }

    // загрузка списка услуг из бд
    private void loadServices() {
        servicesTable.getItems().setAll(serviceDao.getAll());
    }

    // открыть окно добавления услуги
    @FXML
    private void addService() {
        openAddEditWindow("/admin.Shokina/add-service.fxml", "Добавить услугу", null);
    }

    // открыть окно редактирования выбранной услуги
    @FXML
    private void editService() {
        Service selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите услугу для редактирования");
            return;
        }
        openAddEditWindow("/admin.Shokina/change-service.fxml", "Редактировать услугу", selected);
    }

    // удалить выбранную услугу
    @FXML
    private void deleteService() {
        Service selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите услугу для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удаление услуги");
        confirm.setContentText("Вы уверены, что хотите удалить " + selected.getName() + "?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (serviceDao.delete(selected.getId())) {
                loadServices();   // обновить таблицу после удаления
            } else {
                showAlert("Ошибка", "Не удалось удалить услугу");
            }
        }
    }

    // универсальный метод для открытия окон добавления и редактирования
    private void openAddEditWindow(String fxmlPath, String title, Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (service != null) {
                Object controller = loader.getController();
                if (controller instanceof ChangeServiceController) {
                    ((ChangeServiceController) controller).setService(service);
                }
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();       // ждём закрытия окна
            loadServices();            // обновить таблицу
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно: " + title);
        }
    }

    // переход на страницу сотрудников
    @FXML private void goToEmployees() { openWindow("/admin.Shokina/employees.fxml", "Сотрудники"); }

    // переход на страницу расходников
    @FXML private void goToConsumables() { openWindow("/admin.Shokina/consumables.fxml", "Расходники"); }

    // текущая страница услуг
    @FXML private void goToServices() { /* уже здесь */ }

    // переход на страницу клиентов
    @FXML private void goToClients() { openWindow("/admin.Shokina/clientele.fxml", "Клиенты"); }

    // возврат в главное меню
    @FXML private void goBack() { openWindow("/admin.Shokina/menu.fxml", "Главное меню"); }

    // универсальный метод для переключения окон
    private void openWindow(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) servicesTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно: " + title);
        }
    }

    // показать сообщение об ошибке
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}