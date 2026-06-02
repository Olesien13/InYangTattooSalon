package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.MasterDao;
import models.Master;
import java.util.List;

// контроллер окна добавления сотрудника

public class AddEmployeeController {

    @FXML private TextField nameField;                    // поле фио
    @FXML private TextField phoneField;                   // поле телефон
    @FXML private ComboBox<String> positionComboBox;      // выбор должности
    @FXML private TextField hireDateField;                // дата трудоустройства
    @FXML private TextField salaryField;                  // зарплата
    @FXML private Label errorLabel;                       // метка ошибок

    private MasterDao masterDao;   // dao для работы с мастерами

    @FXML
    public void initialize() {
        masterDao = new MasterDao();
        loadPositions();   // загрузка списка должностей
    }

    // загрузка списка должностей
    private void loadPositions() {
        List<String> positions = masterDao.getAllPositions();
        positionComboBox.getItems().setAll(positions);
        if (!positions.isEmpty()) {
            positionComboBox.setValue(positions.get(0));   // выбор первой должности по умолчанию
        }
    }

    // сохранение нового сотрудника
    @FXML
    private void saveEmployee() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите имя сотрудника");
            return;
        }
        if (positionComboBox.getValue() == null || positionComboBox.getValue().isEmpty()) {
            showError("Выберите должность");
            return;
        }
        if (hireDateField.getText().trim().isEmpty()) {
            showError("Введите дату трудоустройства");
            return;
        }

        Master master = new Master();
        master.setName(nameField.getText().trim());
        master.setPhone(phoneField.getText().trim());
        master.setSpecialization(positionComboBox.getValue().trim());
        master.setHireDate(hireDateField.getText().trim());
        master.setActive(true);
        master.setRating(0.0);

        double salary = 0;
        if (!salaryField.getText().trim().isEmpty()) {
            try {
                salary = Double.parseDouble(salaryField.getText().trim());
            } catch (NumberFormatException e) {
                salary = 0;
            }
        }
        master.setSalary(salary);

        int masterId = masterDao.createAndGetId(master);

        if (masterId > 0) {
            if (salary > 0) {
                masterDao.saveSalary(masterId, salary);
            }
            closeWindow();
        } else {
            showError("Ошибка при сохранении сотрудника");
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