package controllers.admin;

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
import java.util.List;

public class ServicesController {

    // Элементы таблицы
    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, Integer> colId;
    @FXML private TableColumn<Service, String> colName;
    @FXML private TableColumn<Service, Integer> colDuration;
    @FXML private TableColumn<Service, String> colMaster;
    @FXML private TableColumn<Service, Double> colPrice;

    // Кнопки навигации
    @FXML private Button employeesMenuBtn;
    @FXML private Button consumablesMenuBtn;
    @FXML private Button servicesMenuBtn;
    @FXML private Button clientsMenuBtn;
    @FXML private Button backButton;

    private ServiceDao serviceDao;

    // Инициализация контроллера
    @FXML
    public void initialize() {
        serviceDao = new ServiceDao();

        // Настройка колонок таблицы
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        colMaster.setCellValueFactory(new PropertyValueFactory<>("masterName"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));

        // Загрузка данных из базы
        loadServices();
    }

    // Загрузка списка услуг из базы данных
    private void loadServices() {
        List<Service> services = serviceDao.getAll();
        servicesTable.getItems().setAll(services);
    }

    // Открыть окно добавления новой услуги
    @FXML
    private void addService() {
        openAddEditWindow("/admin/add-service.fxml", "Добавить услугу", null);
    }

    // Открыть окно редактирования выбранной услуги
    @FXML
    private void editService() {
        Service selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите услугу для редактирования");
            return;
        }
        openAddEditWindow("/admin/change-service.fxml", "Редактировать услугу", selected);
    }

    // Удалить выбранную услугу
    @FXML
    private void deleteService() {
        Service selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите услугу для удаления");
            return;
        }

        // Подтверждение удаления
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удаление услуги");
        confirm.setContentText("Вы уверены, что хотите удалить " + selected.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean deleted = serviceDao.delete(selected.getId());
            if (deleted) {
                loadServices(); // Обновляем таблицу после удаления
            } else {
                showAlert("Ошибка", "Не удалось удалить услугу");
            }
        }
    }

    // Универсальный метод для открытия окон добавления и редактирования
    private void openAddEditWindow(String fxmlPath, String title, Service service) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            // Если передан объект услуги, передаём его в контроллер редактирования
            if (service != null) {
                Object controller = loader.getController();
                if (controller instanceof ChangeServiceController) {
                    ((ChangeServiceController) controller).setService(service);
                }
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadServices(); // Обновляем таблицу после закрытия окна
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно: " + title);
        }
    }

    // Навигация по меню

    // Переход на страницу сотрудников
    @FXML
    private void goToEmployees() {
        openWindow("/admin/employees.fxml", "Сотрудники");
    }

    // Переход на страницу расходников
    @FXML
    private void goToConsumables() {
        openWindow("/admin/consumables.fxml", "Расходники");
    }

    // Переход на страницу услуг
    @FXML
    private void goToServices() {
    }

    // Переход на страницу клиентов
    @FXML
    private void goToClients() {
        openWindow("/admin/clientele.fxml", "Клиенты");
    }

    // Возврат в главное меню
    @FXML
    private void goBack() {
        openWindow("/admin/menu.fxml", "Главное меню");
    }

    // Универсальный метод для переключения окон
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

    // Показать сообщение об ошибке
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}