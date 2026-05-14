package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.MasterDao;
import models.Master;

import java.util.List;

public class ChangeEmployeeController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private ComboBox<String> positionComboBox;
    @FXML private TextField hireDateField;
    @FXML private TextField salaryField;
    @FXML private Label errorLabel;

    private MasterDao masterDao;
    private Master currentMaster;

    @FXML
    public void initialize() {
        masterDao = new MasterDao();
        loadPositions();
    }

    // Загрузка списка должностей из базы данных
    private void loadPositions() {
        List<String> positions = masterDao.getAllPositions();
        positionComboBox.getItems().setAll(positions);
    }

    // Передача данных из EmployeesController
    public void setMaster(Master master) {
        this.currentMaster = master;

        // Заполняем поля формы
        nameField.setText(master.getName());
        phoneField.setText(master.getPhone());
        hireDateField.setText(master.getHireDate());
        salaryField.setText(String.valueOf(master.getSalary()));

        // Устанавливаем выбранную должность в ComboBox
        if (master.getSpecialization() != null && !master.getSpecialization().isEmpty()) {
            positionComboBox.setValue(master.getSpecialization());
        }
    }

    @FXML
    private void updateEmployee() {
        // Проверка на пустые поля
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

        // Обновляем данные мастера
        currentMaster.setName(nameField.getText().trim());
        currentMaster.setPhone(phoneField.getText().trim());
        currentMaster.setSpecialization(positionComboBox.getValue().trim());
        currentMaster.setHireDate(hireDateField.getText().trim());

        // Получаем зарплату
        double salary = 0;
        if (!salaryField.getText().trim().isEmpty()) {
            try {
                salary = Double.parseDouble(salaryField.getText().trim());
            } catch (NumberFormatException e) {
                salary = 0;
            }
        }
        currentMaster.setSalary(salary);

        // Обновляем мастера в БД
        boolean updated = masterDao.update(currentMaster);

        if (updated) {
            // Обновляем зарплату
            masterDao.updateSalary(currentMaster.getId(), salary);
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