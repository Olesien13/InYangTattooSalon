package coursework.itms206.controllers.admin.Shokina;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import coursework.itms206.dao.MasterDao;
import coursework.itms206.models.Master;

import java.util.List;

// контроллер окна редактирования сотрудника

public class ChangeEmployeeController {

    @FXML private TextField nameField;                    // поле фио
    @FXML private TextField phoneField;                   // поле телефон
    @FXML private ComboBox<String> positionComboBox;      // выбор должности
    @FXML private TextField hireDateField;                // дата трудоустройства
    @FXML private TextField salaryField;                  // зарплата
    @FXML private Label errorLabel;                       // метка ошибок

    private MasterDao masterDao;       // dao для работы с мастерами
    private Master currentMaster;      // редактируемый сотрудник

    @FXML
    public void initialize() {
        masterDao = new MasterDao();
        loadPositions();   // загрузка списка должностей
    }

    // загрузка списка должностей из бд
    private void loadPositions() {
        List<String> positions = masterDao.getAllPositions();
        positionComboBox.getItems().setAll(positions);
    }

    // передача данных из EmployeesController
    public void setMaster(Master master) {
        this.currentMaster = master;

        nameField.setText(master.getName());
        phoneField.setText(master.getPhone());
        hireDateField.setText(master.getHireDate());
        salaryField.setText(String.valueOf(master.getSalary()));

        if (master.getSpecialization() != null && !master.getSpecialization().isEmpty()) {
            positionComboBox.setValue(master.getSpecialization());
        }
    }

    // сохранение изменений
    @FXML
    private void updateEmployee() {
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

        currentMaster.setName(nameField.getText().trim());
        currentMaster.setPhone(phoneField.getText().trim());
        currentMaster.setSpecialization(positionComboBox.getValue().trim());
        currentMaster.setHireDate(hireDateField.getText().trim());

        double salary = 0;
        if (!salaryField.getText().trim().isEmpty()) {
            try {
                salary = Double.parseDouble(salaryField.getText().trim());
            } catch (NumberFormatException e) {
                salary = 0;
            }
        }
        currentMaster.setSalary(salary);

        boolean updated = masterDao.update(currentMaster);

        if (updated) {
            masterDao.updateSalary(currentMaster.getId(), salary);
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