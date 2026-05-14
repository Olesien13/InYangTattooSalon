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

public class ServicesController {

    @FXML private TableView<Service> servicesTable;
    @FXML private TableColumn<Service, Integer> colId;
    @FXML private TableColumn<Service, String> colName;
    @FXML private TableColumn<Service, Integer> colDuration;
    @FXML private TableColumn<Service, Double> colPrice;

    private ServiceDao serviceDao;

    @FXML
    public void initialize() {
        serviceDao = new ServiceDao();
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colDuration.setCellValueFactory(new PropertyValueFactory<>("durationMinutes"));
        colPrice.setCellValueFactory(new PropertyValueFactory<>("price"));
        loadServices();
    }

    private void loadServices() {
        servicesTable.getItems().setAll(serviceDao.getAll());
    }

    @FXML
    private void addService() {
        openAddEditWindow("/admin/add-service.fxml", "Добавить услугу", null);
    }

    @FXML
    private void editService() {
        Service selected = servicesTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert("Ошибка", "Выберите услугу для редактирования");
            return;
        }
        openAddEditWindow("/admin/change-service.fxml", "Редактировать услугу", selected);
    }

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
                loadServices();
            } else {
                showAlert("Ошибка", "Не удалось удалить услугу");
            }
        }
    }

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
            stage.showAndWait();
            loadServices();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно");
        }
    }

    @FXML private void goToEmployees() { openWindow("/admin/employees.fxml", "Сотрудники"); }
    @FXML private void goToConsumables() { openWindow("/admin/consumables.fxml", "Расходники"); }
    @FXML private void goToServices() { }
    @FXML private void goToClients() { openWindow("/admin/clientele.fxml", "Клиенты"); }
    @FXML private void goBack() { openWindow("/admin/menu.fxml", "Главное меню"); }

    private void openWindow(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
            Stage stage = (Stage) servicesTable.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Ошибка", "Не удалось открыть окно");
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}