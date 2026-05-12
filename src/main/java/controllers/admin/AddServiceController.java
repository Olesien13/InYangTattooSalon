package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.MasterDao;
import dao.ServiceDao;
import models.Master;
import models.Service;
import java.util.List;

public class AddServiceController {

    @FXML private TextField nameField;
    @FXML private TextField durationField;
    @FXML private ComboBox<Master> masterComboBox;
    @FXML private TextField priceField;
    @FXML private Label errorLabel;

    private ServiceDao serviceDao;
    private MasterDao masterDao;

    @FXML
    public void initialize() {
        serviceDao = new ServiceDao();
        masterDao = new MasterDao();
        loadMasters();
    }

    private void loadMasters() {
        List<Master> masters = masterDao.getAll();
        masterComboBox.getItems().setAll(masters);

        masterComboBox.setCellFactory(lv -> new ListCell<Master>() {
            @Override
            protected void updateItem(Master master, boolean empty) {
                super.updateItem(master, empty);
                setText(empty ? null : master.getName());
            }
        });
        masterComboBox.setButtonCell(new ListCell<Master>() {
            @Override
            protected void updateItem(Master master, boolean empty) {
                super.updateItem(master, empty);
                setText(empty ? null : master.getName());
            }
        });
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
            int duration = Integer.parseInt(durationField.getText().trim());
            service.setDurationMinutes(duration);
        } catch (NumberFormatException e) {
            showError("Введите корректное время выполнения");
            return;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            service.setPrice(price);
        } catch (NumberFormatException e) {
            showError("Введите корректную цену");
            return;
        }

        Master selectedMaster = masterComboBox.getValue();
        if (selectedMaster == null) {
            showError("Выберите мастера");
            return;
        }
        service.setMasterId(selectedMaster.getId());

        boolean created = serviceDao.create(service);
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