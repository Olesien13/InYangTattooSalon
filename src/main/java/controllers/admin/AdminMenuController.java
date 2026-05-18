package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class AdminMenuController {

    @FXML private Button employeesBtn;
    @FXML private Button consumablesBtn;
    @FXML private Button servicesBtn;
    @FXML private Button clientsBtn;
    @FXML private Button backButton;
    @FXML private Button logoutBtn;  // Добавьте эту строку, если нужно

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
        openWindow("/admin/clientele.fxml", "Клиенты");
    }

    @FXML
    private void goToStatistics() {
        openWindow("/admin/statistics.fxml", "Статистика");
    }

    // Кнопка "Вернуться назад" - выход из аккаунта
    @FXML
    private void logout() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/client/login-view.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Вход");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) employeesBtn.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Не удалось открыть: " + fxmlPath);
        }
    }
}