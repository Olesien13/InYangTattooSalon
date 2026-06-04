package coursework.itms206.controllers.client.Kholenko;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import coursework.itms206.dao.UserDao;
import coursework.itms206.models.User;
import coursework.itms206.utils.PasswordUtils;

public class RegisterController {

    // поля, соответствующие элементам fxml
    @FXML private TextField emailField;               // поле ввода email
    @FXML private PasswordField passwordField;        // поле ввода пароля
    @FXML private PasswordField confirmPasswordField; // поле повторного ввода пароля
    @FXML private Label errorLabel;// метка для показа ошибок

    private UserDao userDao;

    @FXML
    public void initialize() {
        userDao = new UserDao();
    }

    // обработчик кнопки "Зарегистрироваться"
    @FXML
    private void registerButton(ActionEvent event) {

        // получаем данные из полей
        String email = emailField.getText().trim();
        String password = passwordField.getText();
        String confirm = confirmPasswordField.getText();

        // проверка на пустые поля
        if (email.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            showError("Заполните email, пароль и подтверждение");
            return;
        }

        // проверка совпадения паролей
        if (!password.equals(confirm)) {
            showError("Пароли не совпадают");
            return;
        }

        // проверка минимальной длины пароля
        if (password.length() < 4) {
            showError("Пароль не менее 4 символов");
            return;
        }

        // проверка уникальности email
        if (userDao.findByEmail(email) != null) {
            showError("Пользователь с таким email уже существует");
            return;
        }

        // хешируем пароль и создаем нового пользователя с ролью "Клиент"
        String passwordHash = PasswordUtils.hashPassword(password);
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setPasswordHash(passwordHash);
            newUser.setRole("client");
            newUser.setFirstName("");
            newUser.setLastName("");
            newUser.setPhone("");

        if (userDao.create(newUser)) {

            // переходим на окно входа
            try {
                Parent root = FXMLLoader.load(getClass().getResource("/client.Kholenko/login-view.fxml"));
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (Exception e) {
                e.printStackTrace();
                showError("Ошибка перехода: " + e.getMessage());
            }
        } else {
            showError("Ошибка регистрации");
        }
    }

    // переход обратно на окно входа (по кнопке "ВХОД")
    @FXML
    private void backLogin(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/client.Kholenko/login-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // метод для сообщения об ошибке в метке errorLabel
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}