package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.ClientDao;
import models.Client;

import java.io.IOException;
import java.util.List;

public class ClientsController {

    // Элементы таблицы
    @FXML private TableView<Client> clientsTable;
    @FXML private TableColumn<Client, Integer> colId;
    @FXML private TableColumn<Client, String> colName;
    @FXML private TableColumn<Client, String> colPhone;
    @FXML private TableColumn<Client, String> colEmail;
    @FXML private TableColumn<Client, String> colServiceName;
    @FXML private TableColumn<Client, Double> colTotalSpent;
    @FXML private TableColumn<Client, String> colRegistrationDate;
    @FXML private TableColumn<Client, String> colStatus;
    @FXML private TableColumn<Client, String> colSize;

    // Кнопки навигации
    @FXML private Button employeesMenuBtn;
    @FXML private Button consumablesMenuBtn;
    @FXML private Button servicesMenuBtn;
    @FXML private Button clientsMenuBtn;
    @FXML private Button backButton;

    // Кнопки действий
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;

    private ClientDao clientDao;


    // Инициализация контроллера
    @FXML
    public void initialize() {
        clientDao = new ClientDao();

        // Настройка колонок таблицы
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colServiceName.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        colTotalSpent.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadClients();
    }

    // Загрузка списка клиентов
    private void loadClients() {
        List<Client> clients = clientDao.getAll();
        clientsTable.getItems().setAll(clients);
    }

    // Открыть окно добавления клиента
    @FXML
    private void addClient() {
        openAddEditWindow("/admin/add-clientele.fxml", "Добавить клиента", null);
    }

    // Открыть окно редактирования выбранного клиента
    @FXML
    private void editClient() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите клиента для редактирования");
            return;
        }
        openAddEditWindow("/admin/change-clientele.fxml", "Редактировать клиента", selected);
    }

    // Удалить выбранного клиента
    @FXML
    private void deleteClient() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите клиента для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удаление клиента");
        confirm.setContentText("Вы уверены, что хотите удалить клиента " + selected.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean deleted = clientDao.delete(selected.getId());
            if (deleted) {
                loadClients();
                showAlert("Успех", "Клиент успешно удалён");
            } else {
                showAlert("Ошибка", "Не удалось удалить клиента");
            }
        }
    }

    // Универсальный метод для открытия окон добавления и редактирования
    private void openAddEditWindow(String fxmlPath, String title, Client client) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (client != null) {
                Object controller = loader.getController();
                if (controller instanceof ChangeClientController) {
                    ((ChangeClientController) controller).setClient(client);
                }
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadClients();
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
        openWindow("/admin/service.fxml", "Услуги");
    }

    // Переход на страницу клиентов (текущая страница)
    @FXML
    private void goToClients() {
        // Уже на этой странице
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
            Stage stage = (Stage) clientsTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно: " + title);
        }
    }

    // Показать сообщение
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}