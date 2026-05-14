package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.ServiceDao;
import models.Service;

public class AddServiceController {

    @FXML private TextField nameField;
    @FXML private TextField durationField;
    @FXML private TextField priceField;
    @FXML private Label errorLabel;

    private ServiceDao serviceDao;

    @FXML
    public void initialize() {
        serviceDao = new ServiceDao();
    }

    @FXML
    private void saveService() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите название услуги");
            return;
        }

        Service service = new Service();
        service.setName(nameField.getText().trim());

        try {
            service.setDurationMinutes(Integer.parseInt(durationField.getText().trim()));
        } catch (NumberFormatException e) {
            showError("Введите корректное время выполнения");
            return;
        }

        try {
            service.setPrice(Double.parseDouble(priceField.getText().trim()));
        } catch (NumberFormatException e) {
            showError("Введите корректную цену");
            return;
        }

        if (serviceDao.create(service)) {
            closeWindow();
        } else {
            showError("Ошибка при сохранении");
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