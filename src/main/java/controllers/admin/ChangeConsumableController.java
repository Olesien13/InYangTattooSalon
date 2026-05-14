package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.ConsumableDao;
import models.Consumable;

public class ChangeConsumableController {

    @FXML private TextField nameField;
    @FXML private TextField quantityField;
    @FXML private TextField priceField;
    @FXML private TextField unitField;
    @FXML private Label errorLabel;

    private ConsumableDao consumableDao;
    private Consumable currentConsumable;

    @FXML
    public void initialize() {
        consumableDao = new ConsumableDao();
    }

    public void setConsumable(Consumable consumable) {
        this.currentConsumable = consumable;

        nameField.setText(consumable.getName());
        quantityField.setText(String.valueOf(consumable.getQuantity()));
        priceField.setText(String.valueOf(consumable.getPrice()));
        unitField.setText(consumable.getUnit());
    }

    @FXML
    private void updateConsumable() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите название расходника");
            return;
        }

        currentConsumable.setName(nameField.getText().trim());
        currentConsumable.setUnit(unitField.getText().trim());

        try {
            currentConsumable.setQuantity(Integer.parseInt(quantityField.getText().trim()));
        } catch (NumberFormatException e) {
            currentConsumable.setQuantity(0);
        }

        try {
            currentConsumable.setPrice(Double.parseDouble(priceField.getText().trim()));
        } catch (NumberFormatException e) {
            currentConsumable.setPrice(0);
        }

        boolean updated = consumableDao.update(currentConsumable);
        if (updated) {
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