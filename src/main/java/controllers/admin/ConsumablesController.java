package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import dao.ConsumableDao;
import models.Consumable;

import java.io.IOException;
import java.util.List;

public class ConsumablesController {

    @FXML private TableView<Consumable> consumablesTable;
    @FXML private TableColumn<Consumable, Integer> colId;
    @FXML private TableColumn<Consumable, String> colName;
    @FXML private TableColumn<Consumable, Integer> colQuantity;
    @FXML private TableColumn<Consumable, Double> colPrice;
    @FXML private TableColumn<Consumable, String> colUnit;

    @FXML private Button addButton;
    @FXML private Button editButton;
    @FXML private Button deleteButton;
    @FXML private Button backButton;

    private ConsumableDao consumableDao;

    @FXML
    public void initialize() {
        consumableDao = new ConsumableDao();

        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        loadConsumables();
    }

    private void loadConsumables() {
        List<Consumable> consumables = consumableDao.getAll();
        consumablesTable.getItems().setAll(consumables);
    }

    @FXML
    private void addConsumable() {
        openAddEditWindow("/admin/add-consumables.fxml", "Добавить расходник", null);
    }

    @FXML
    private void editConsumable() {
        Consumable selected = consumablesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите расходник для редактирования");
            return;
        }
        openAddEditWindow("/admin/change-consumables.fxml", "Редактировать расходник", selected);
    }

    @FXML
    private void deleteConsumable() {
        Consumable selected = consumablesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите расходник для удаления");
            return;
        }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Подтверждение");
        confirm.setHeaderText("Удаление расходника");
        confirm.setContentText("Вы уверены, что хотите удалить " + selected.getName() + "?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            boolean deleted = consumableDao.delete(selected.getId());
            if (deleted) {
                loadConsumables();
            } else {
                showAlert("Ошибка", "Не удалось удалить расходник");
            }
        }
    }

    private void openAddEditWindow(String fxmlPath, String title, Consumable consumable) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (consumable != null) {
                // Передаём данные в контроллер редактирования
                Object controller = loader.getController();
                if (controller instanceof ChangeConsumableController) {
                    ((ChangeConsumableController) controller).setConsumable(consumable);
                }
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadConsumables();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно: " + title);
        }
    }

    @FXML
    private void goBack() {
        openWindow("/admin/menu.fxml", "Главное меню");
    }

    @FXML
    private void goToEmployees() {
        openWindow("/admin/employees.fxml", "Сотрудники");
    }

    @FXML
    private void goToConsumables() {
    }

    @FXML
    private void goToServices() {
        openWindow("/admin/service.fxml", "Услуги");
    }

    @FXML
    private void goToClients() {
        openWindow("/admin/clientele.fxml", "Клиенты");
    }

    private void openWindow(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) consumablesTable.getScene().getWindow();
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