package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import java.io.IOException;

// контроллер главного меню администратора

public class AdminMenuController {

    // кнопки меню
    @FXML private Button employeesBtn;
    @FXML private Button consumablesBtn;
    @FXML private Button servicesBtn;
    @FXML private Button clientsBtn;
    @FXML private Button statisticsBtn;
    @FXML private Button backButton;

    // переход к сотрудникам
    @FXML
    private void goToEmployees() {
        openWindow("/admin/employees.fxml", "Сотрудники");
    }

    // переход к расходникам
    @FXML
    private void goToConsumables() {
        openWindow("/admin/consumables.fxml", "Расходники");
    }

    // переход к услугам
    @FXML
    private void goToServices() {
        openWindow("/admin/service.fxml", "Услуги");
    }

    // переход к клиентам
    @FXML
    private void goToClients() {
        openWindow("/admin/clientele.fxml", "Клиенты");
    }

    // переход к статистике
    @FXML
    private void goToStatistics() {
        openWindow("/admin/statistics.fxml", "Статистика");
    }

    // выход из аккаунта (возврат на окно входа)
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

    // универсальный метод для открытия окон
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