package coursework.itms206.controllers.admin.Shokina;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import coursework.itms206.dao.ConsumableDao;
import coursework.itms206.models.Consumable;

// контроллер окна редактирования расходного материала

public class ChangeConsumableController {

    @FXML private TextField nameField;        // поле название
    @FXML private TextField quantityField;    // поле количество
    @FXML private TextField priceField;       // поле цена
    @FXML private TextField unitField;        // поле единица измерения
    @FXML private Label errorLabel;           // метка ошибок

    private ConsumableDao consumableDao;      // dao для работы с расходниками
    private Consumable currentConsumable;     // редактируемый расходник

    @FXML
    public void initialize() {
        consumableDao = new ConsumableDao();
    }

    // передача данных из ConsumablesController
    public void setConsumable(Consumable consumable) {
        this.currentConsumable = consumable;

        nameField.setText(consumable.getName());
        quantityField.setText(String.valueOf(consumable.getQuantity()));
        priceField.setText(String.valueOf(consumable.getPrice()));
        unitField.setText(consumable.getUnit());
    }

    // сохранение изменений
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