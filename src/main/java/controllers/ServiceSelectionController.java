package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dao.ServiceDao;
import models.Service;
import utils.UserSession;

import java.util.List;

public class ServiceSelectionController {

    @FXML private VBox servicesContainer;
    @FXML private Label errorLabel;

    private ServiceDao serviceDao;

    @FXML
    public void initialize() {
        serviceDao = new ServiceDao();
        loadServices();
    }

    private void loadServices() {
        servicesContainer.getChildren().clear();

        List<Service> services = serviceDao.getAll();

        if (services.isEmpty()) {
            showError("Нет доступных услуг");
            return;
        }

        for (Service service : services) {
            Button serviceButton = new Button(service.getName() + " - " + service.getPrice() + " ₽");
            serviceButton.setMaxWidth(Double.MAX_VALUE);
            serviceButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

            serviceButton.setOnAction(event -> selectService(service, event));

            servicesContainer.getChildren().add(serviceButton);
        }
    }

    private void selectService(Service service, ActionEvent event) {
        // Сохраняем выбранную услугу в сессию
        UserSession.setSelectedServiceId(service.getId());

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/client/master-selection.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка перехода: " + e.getMessage());
        }
    }

    @FXML
    private void goBack(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/client/main-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}