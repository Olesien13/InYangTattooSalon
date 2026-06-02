package controllers.admin.Shokina;

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

// контроллер окна управления клиентами

public class ClientsController {

    // таблица и колонки
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

    // кнопки навигации
    @FXML private Button employeesMenuBtn;
    @FXML private Button consumablesMenuBtn;
    @FXML private Button servicesMenuBtn;
    @FXML private Button clientsMenuBtn;
    @FXML private Button backButton;

    // кнопки действий
    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button consumablesReportBtn;

    private ClientDao clientDao;

    @FXML
    public void initialize() {
        clientDao = new ClientDao();

        // привязка колонок к полям модели Client
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colServiceName.setCellValueFactory(new PropertyValueFactory<>("serviceName"));
        colTotalSpent.setCellValueFactory(new PropertyValueFactory<>("totalSpent"));
        colSize.setCellValueFactory(new PropertyValueFactory<>("size"));
        colRegistrationDate.setCellValueFactory(new PropertyValueFactory<>("registrationDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        loadClients(); // загрузка списка
    }

    // загрузка списка клиентов из бд
    private void loadClients() {
        List<Client> clients = clientDao.getAll();
        clientsTable.getItems().setAll(clients);
    }

    // открыть окно добавления клиента
    @FXML
    private void addClient() {
        openAddEditWindow("/admin.Shokina/add-clientele.fxml", "Добавить клиента", null);
    }

    // открыть окно редактирования выбранного клиента
    @FXML
    private void editClient() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите клиента для редактирования");
            return;
        }
        openAddEditWindow("/admin.Shokina/change-clientele.fxml", "Редактировать клиента", selected);
    }

    // удалить выбранного клиента
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
            if (clientDao.delete(selected.getId())) {
                loadClients();
                showAlert("Успех", "Клиент успешно удалён");
            } else {
                showAlert("Ошибка", "Не удалось удалить клиента");
            }
        }
    }

    // открыть отчёт по расходникам клиента
    @FXML
    private void showClientConsumables() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите клиента");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin.Shokina/client-consumables-report.fxml"));
            Parent root = loader.load();

            ClientConsumablesReportController controller = loader.getController();
            controller.setClient(selected);

            Stage stage = new Stage();
            stage.setTitle("Расходники клиента");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть отчёт по расходникам");
        }
    }

    // универсальный метод для открытия окон добавления и редактирования
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

    // переход на страницу сотрудников
    @FXML
    private void goToEmployees() {
        openWindow("/admin.Shokina/employees.fxml", "Сотрудники");
    }

    // переход на страницу расходников
    @FXML
    private void goToConsumables() {
        openWindow("/admin.Shokina/consumables.fxml", "Расходники");
    }

    // переход на страницу услуг
    @FXML
    private void goToServices() {
        openWindow("/admin.Shokina/service.fxml", "Услуги");
    }

    // текущая страница клиентов
    @FXML
    private void goToClients() {
        // уже здесь
    }

    // возврат в главное меню
    @FXML
    private void goBack() {
        openWindow("/admin.Shokina/menu.fxml", "Главное меню");
    }

    // универсальный метод для переключения окон
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

    // показать сообщение
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}