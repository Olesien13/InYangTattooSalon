package controllers.admin.Shokina;

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

// контроллер окна управления расходными материалами

public class ConsumablesController {

    @FXML private TableView<Consumable> consumablesTable;   // таблица расходников
    @FXML private TableColumn<Consumable, Integer> colId;    // колонка id
    @FXML private TableColumn<Consumable, String> colName;   // колонка название
    @FXML private TableColumn<Consumable, Integer> colQuantity; // колонка количество
    @FXML private TableColumn<Consumable, Double> colPrice;  // колонка цена
    @FXML private TableColumn<Consumable, String> colUnit;   // колонка единица измерения

    @FXML private Button addButton;     // кнопка добавления
    @FXML private Button editButton;    // кнопка редактирования
    @FXML private Button deleteButton;  // кнопка удаления
    @FXML private Button backButton;    // кнопка назад

    private ConsumableDao consumableDao;   // dao для работы с расходниками

    @FXML
    public void initialize() {
        consumableDao = new ConsumableDao();

        // привязка колонок к полям модели Consumable
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        colUnit.setCellValueFactory(new PropertyValueFactory<>("unit"));

        loadConsumables(); // загрузка списка
    }

    // загрузка списка расходников из бд
    private void loadConsumables() {
        List<Consumable> consumables = consumableDao.getAll();
        consumablesTable.getItems().setAll(consumables);
    }

    // открыть окно добавления расходника
    @FXML
    private void addConsumable() {
        openAddEditWindow("/admin.Shokina/add-consumables.fxml", "Добавить расходник", null);
    }

    // открыть окно редактирования выбранного расходника
    @FXML
    private void editConsumable() {
        Consumable selected = consumablesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите расходник для редактирования");
            return;
        }
        openAddEditWindow("/admin.Shokina/change-consumables.fxml", "Редактировать расходник", selected);
    }

    // удалить выбранный расходник
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
            if (consumableDao.delete(selected.getId())) {
                loadConsumables();   // обновить таблицу
            } else {
                showAlert("Ошибка", "Не удалось удалить расходник");
            }
        }
    }

    // универсальный метод для открытия окон добавления и редактирования
    private void openAddEditWindow(String fxmlPath, String title, Consumable consumable) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();

            if (consumable != null) {
                Object controller = loader.getController();
                if (controller instanceof ChangeConsumableController) {
                    ((ChangeConsumableController) controller).setConsumable(consumable);
                }
            }

            Stage stage = new Stage();
            stage.setTitle(title);
            stage.setScene(new Scene(root));
            stage.showAndWait();
            loadConsumables();   // обновить таблицу
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно: " + title);
        }
    }

    // возврат в главное меню
    @FXML
    private void goBack() {
        openWindow("/admin.Shokina/menu.fxml", "Главное меню");
    }

    // переход на страницу сотрудников
    @FXML
    private void goToEmployees() {
        openWindow("/admin.Shokina/employees.fxml", "Сотрудники");
    }

    // текущая страница расходников
    @FXML
    private void goToConsumables() {
        // уже здесь
    }

    // переход на страницу услуг
    @FXML
    private void goToServices() {
        openWindow("/admin.Shokina/service.fxml", "Услуги");
    }

    // переход на страницу клиентов
    @FXML
    private void goToClients() {
        openWindow("/admin.Shokina/clientele.fxml", "Клиенты");
    }

    // универсальный метод для переключения окон
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

    // показать сообщение
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}