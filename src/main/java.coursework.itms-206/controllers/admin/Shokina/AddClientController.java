package controllers.admin.Shokina;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.UserDao;
import models.User;
import utils.PasswordUtils;

// контроллер окна добавления клиента

public class AddClientController {

    @FXML private TextField nameField;                // поле фио
    @FXML private TextField phoneField;               // поле телефон
    @FXML private TextField emailField;               // поле почта
    @FXML private ComboBox<String> serviceComboBox;   // выбор услуги
    @FXML private ComboBox<String> statusComboBox;    // статус
    @FXML private TextField totalSpentField;          // поле суммы
    @FXML private TextField dateField;                // поле даты
    @FXML private Label errorLabel;                   // метка ошибок

    private UserDao userDao;   // dao для работы с пользователями

    @FXML
    public void initialize() {
        userDao = new UserDao();

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

        statusComboBox.getItems().addAll(
                "В обработке",
                "Подтверждено",
                "Выполнено",
                "Отменено"
        );
        statusComboBox.setEditable(false);
        statusComboBox.setValue("В обработке");
    }

    // сохранение нового клиента
    @FXML
    private void saveClient() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите ФИО клиента");
            return;
        }
        if (emailField.getText().trim().isEmpty()) {
            showError("Введите email клиента");
            return;
        }

        if (userDao.findByEmail(emailField.getText().trim()) != null) {
            showError("Пользователь с таким email уже существует");
            return;
        }

        User user = new User();
        user.setEmail(emailField.getText().trim());

        String tempPassword = generateTempPassword();
        user.setPasswordHash(PasswordUtils.hashPassword(tempPassword));

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

    // генерация временного пароля
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