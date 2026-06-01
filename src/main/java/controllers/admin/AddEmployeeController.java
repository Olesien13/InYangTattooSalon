package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.MasterDao;
import models.Master;

import java.util.List;

public class AddEmployeeController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private TextField hireDateField;
    @FXML private TextField salaryField;
    @FXML private Label errorLabel;

    private MasterDao masterDao;

    @FXML
    public void initialize() {
        masterDao = new MasterDao();
        loadPositions();
    }

    private void loadPositions() {
        List<String> positions = masterDao.getAllPositions();
        positionComboBox.getItems().setAll(positions);
        if (!positions.isEmpty()) {
            positionComboBox.setValue(positions.get(0));
        }
    }

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

        // Зарплата
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