package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.UserDao;
import models.User;
import utils.PasswordUtils;

public class AddClientController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> serviceComboBox;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField totalSpentField;
    @FXML private TextField dateField;
    @FXML private Label errorLabel;

    private UserDao userDao;

    @FXML
    public void initialize() {
        userDao = new UserDao();

        // Список услуг для комбобокса
        serviceComboBox.getItems().addAll(
                "Цветное тату",
                "Черно-белое тату",
                "Перекрытие",
                "Коррекция",
                "Сведение",
                "Эскиз",
                "Консультация"
        );
        serviceComboBox.setEditable(true);

        // Список статусов для комбобокса
        statusComboBox.getItems().addAll(
                "В обработке",
                "Подтверждено",
                "Выполнено",
                "Отменено"
        );
        statusComboBox.setEditable(false);
        statusComboBox.setValue("В обработке");
    }

    @FXML
    private void saveClient() {
        // Проверка на пустые поля
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите ФИО клиента");
            return;
        }
        if (emailField.getText().trim().isEmpty()) {
            showError("Введите email клиента");
            return;
        }

        // Проверка на существующего пользователя
        if (userDao.findByEmail(emailField.getText().trim()) != null) {
            showError("Пользователь с таким email уже существует");
            return;
        }

        // Создание нового пользователя
        User user = new User();
        user.setEmail(emailField.getText().trim());

        // Генерация временного пароля
        String tempPassword = generateTempPassword();
        user.setPasswordHash(PasswordUtils.hashPassword(tempPassword));

        // Разделяем ФИО на части
        String[] nameParts = nameField.getText().trim().split(" ", 3);
        String lastName = nameParts[0];
        String firstName = nameParts.length > 1 ? nameParts[1] : "";
        String middleName = nameParts.length > 2 ? nameParts[2] : "";

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setMiddleName(middleName);

        user.setPhone(phoneField.getText().trim());
        user.setRole("client");

        boolean created = userDao.create(user);

        if (created) {
            showSuccess("Клиент успешно добавлен. Временный пароль: " + tempPassword);
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

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Успех");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private String generateTempPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        StringBuilder password = new StringBuilder();
        java.util.Random rand = new java.util.Random();
        for (int i = 0; i < 8; i++) {
            password.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return password.toString();
    }
}