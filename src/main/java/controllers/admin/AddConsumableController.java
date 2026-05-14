package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.ConsumableDao;
import models.Consumable;

public class AddConsumableController {

    @FXML private TextField nameField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private TextField unitField;
    @FXML private Label errorLabel;

    private ConsumableDao consumableDao;

    @FXML
    public void initialize() {
        consumableDao = new ConsumableDao();
        unitField.setText("шт"); // Значение по умолчанию
    }

    @FXML
    private void saveConsumable() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите название расходника");
            return;
        }

        Consumable consumable = new Consumable();
        consumable.setName(nameField.getText().trim());
        consumable.setUnit(unitField.getText().trim());

        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            consumable.setQuantity(quantity);
        } catch (NumberFormatException e) {
            consumable.setQuantity(0);
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            consumable.setPrice(price);
        } catch (NumberFormatException e) {
            consumable.setPrice(0);
        }

        boolean created = consumableDao.create(consumable);
        if (created) {
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