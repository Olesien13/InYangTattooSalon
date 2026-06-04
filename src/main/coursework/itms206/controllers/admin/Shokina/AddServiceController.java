package coursework.itms206.controllers.admin.Shokina;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import coursework.itms206.dao.MasterDao;
import coursework.itms206.dao.ServiceDao;
import coursework.itms206.models.Master;
import coursework.itms206.models.Service;
import java.util.List;

// контроллер окна добавления услуги

public class AddServiceController {

    @FXML private TextField nameField;               // поле название
    @FXML private TextField durationField;           // поле время выполнения
    @FXML private TextField priceField;              // поле цена
    @FXML private Label errorLabel;                  // метка ошибок
    @FXML private ComboBox<Master> masterComboBox;   // выбор мастера

    private ServiceDao serviceDao;    // dao для работы с услугами
    private MasterDao masterDao;      // dao для работы с мастерами

    @FXML
    public void initialize() {
        serviceDao = new ServiceDao();
        masterDao = new MasterDao();
        loadMasters();   // загрузка списка мастеров
    }

    // загрузка списка мастеров в combobox
    private void loadMasters() {
        List<Master> masters = masterDao.getAll();
        masterComboBox.getItems().setAll(masters);

        // отображение имени мастера
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

    // сохранение новой услуги
    @FXML
    private void saveService() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите название услуги");
            return;
        }

        Master selectedMaster = masterComboBox.getValue();
        if (selectedMaster == null) {
            showError("Выберите мастера");
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

        boolean created = serviceDao.create(service);

        if (created) {
            int serviceId = service.getId();
            if (serviceId > 0) {
                serviceDao.linkWithMaster(serviceId, selectedMaster.getId());
            }
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