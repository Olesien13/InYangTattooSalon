package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.ServiceDao;
import models.Service;

public class ChangeServiceController {

    @FXML private TextField nameField;
    @FXML private TextField durationField;
    @FXML private TextField priceField;
    @FXML private Label errorLabel;

    private ServiceDao serviceDao;
    private Service currentService;

    @FXML
    public void initialize() {
        serviceDao = new ServiceDao();
    }

    public void setService(Service service) {
        this.currentService = service;
        nameField.setText(service.getName());
        durationField.setText(String.valueOf(service.getDurationMinutes()));
        priceField.setText(String.valueOf(service.getPrice()));
    }

    @FXML
    private void updateService() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите название услуги");
            return;
        }

        currentService.setName(nameField.getText().trim());

        try {
            currentService.setDurationMinutes(Integer.parseInt(durationField.getText().trim()));
        } catch (NumberFormatException e) {
            showError("Введите корректное время выполнения");
            return;
        }

        try {
            currentService.setPrice(Double.parseDouble(priceField.getText().trim()));
        } catch (NumberFormatException e) {
            showError("Введите корректную цену");
            return;
        }

        if (serviceDao.update(currentService)) {
            closeWindow();
        } else {
            showError("Ошибка при обновлении");
        }
    }

    @FXML
    private void cancel() {
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) nameField.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}