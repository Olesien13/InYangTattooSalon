package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.ClientDao;
import dao.DatabaseConnection;  // ← ДОБАВИТЬ
import models.Client;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
    @FXML private Button consumablesReportBtn;

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

    // Получить последний ID записи клиента
    private int getLastAppointmentId(int clientId) {
        String sql = "SELECT id FROM appointments WHERE user_id = ? ORDER BY appointment_date DESC LIMIT 1";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, clientId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("id");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Отчёт по расходникам клиента
    @FXML
    private void showClientConsumables() {
        Client selected = clientsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите клиента");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/admin/client-consumables-report.fxml"));
            Parent root = loader.load();

            ClientConsumablesReportController controller = loader.getController();
            controller.setClient(selected);  // Передаём клиента, а не appointmentId

            Stage stage = new Stage();
            stage.setTitle("Расходники клиента");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть отчёт по расходникам");
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

    // ==================== Навигация по меню ====================

    @FXML
    private void goToEmployees() {
        openWindow("/admin/employees.fxml", "Сотрудники");
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
        // Уже на этой странице
    }

    @FXML
    private void goBack() {
        openWindow("/admin/menu.fxml", "Главное меню");
    }

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

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}