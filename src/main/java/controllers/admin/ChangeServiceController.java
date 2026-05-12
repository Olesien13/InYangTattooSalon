package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.MasterDao;
import dao.ServiceDao;
import models.Master;
import models.Service;
import java.util.List;

public class ChangeServiceController {

    @FXML private TextField nameField;
    @FXML private TextField durationField;
    @FXML private ComboBox<Master> masterComboBox;
    @FXML private TextField priceField;
    @FXML private Label errorLabel;

    private ServiceDao serviceDao;
    private MasterDao masterDao;
    private Service currentService;

    @FXML
    public void initialize() {
        serviceDao = new ServiceDao();
        masterDao = new MasterDao();
        loadMasters();
    }

    private void loadMasters() {
        List<Master> masters = masterDao.getAll();
        masterComboBox.getItems().setAll(masters);

        // Отображаем имя мастера в выпадающем списке
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

    public void setService(Service service) {
        this.currentService = service;

        nameField.setText(service.getName());
        durationField.setText(String.valueOf(service.getDurationMinutes()));
        priceField.setText(String.valueOf(service.getPrice()));

        // Выбираем мастера в ComboBox
        masterComboBox.getItems().forEach(master -> {
            if (master.getId() == service.getMasterId()) {
                masterComboBox.setValue(master);
            }
        });
    }

    @FXML
    private void updateService() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите название услуги");
            return;
        }

        currentService.setName(nameField.getText().trim());

        try {
            int duration = Integer.parseInt(durationField.getText().trim());
            currentService.setDurationMinutes(duration);
        } catch (NumberFormatException e) {
            showError("Введите корректное время выполнения");
            return;
        }

        try {
            double price = Double.parseDouble(priceField.getText().trim());
            currentService.setPrice(price);
        } catch (NumberFormatException e) {
            showError("Введите корректную цену");
            return;
        }

        Master selectedMaster = masterComboBox.getValue();
        if (selectedMaster == null) {
            showError("Выберите мастера");
            return;
        }
        currentService.setMasterId(selectedMaster.getId());

        boolean updated = serviceDao.update(currentService);
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