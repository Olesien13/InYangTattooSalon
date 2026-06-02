package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.ConsumableDao;
import models.Consumable;

// контроллер окна добавления расходного материала

public class AddConsumableController {

    @FXML private TextField nameField;        // поле название
    @FXML private TextField quantityField;    // поле количество
    @FXML private TextField priceField;       // поле цена
    @FXML private TextField unitField;        // поле единица измерения
    @FXML private Label errorLabel;           // метка ошибок

    private ConsumableDao consumableDao;   // dao для работы с расходниками

    @FXML
    public void initialize() {
        consumableDao = new ConsumableDao();
        unitField.setText("шт");   // значение по умолчанию
    }

    // сохранение нового расходника
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
            consumable.setQuantity(Integer.parseInt(quantityField.getText().trim()));
        } catch (NumberFormatException e) {
            consumable.setQuantity(0);
        }

        try {
            consumable.setPrice(Double.parseDouble(priceField.getText().trim()));
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

    // отмена и закрытие
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