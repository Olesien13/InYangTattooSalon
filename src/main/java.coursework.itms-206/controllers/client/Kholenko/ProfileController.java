package controllers.client.Kholenko;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dao.UserDao;
import models.User;
import utils.UserSession;
import utils.PasswordUtils;

// контроллер для окна профиля пользователя
public class ProfileController {

    // поля для текстовых полей ввода из fxml
    @FXML private TextField lastNameField;      // поле фамилии
    @FXML private TextField firstNameField;     // поле имени
    @FXML private TextField middleNameField;    // поле отчества
    @FXML private TextField phoneField;         // поле телефона
    @FXML private TextField emailField;         // поле email

    // поля для кнопок
    @FXML private Button editButton;             // кнопка "редактировать"
    @FXML private Button saveButton;             // кнопка "сохранить"
    @FXML private Button changePasswordButton;   // кнопка "сменить пароль"
    @FXML private Button logoutButton;           // кнопка "выйти"

    // поля для отображения бонусов
    @FXML private Label discountLabel;           // метка для текущей скидки
    @FXML private Label nextLevelLabel;          // метка для следующего уровня скидки
    @FXML private ProgressBar progressBar;       // прогресс-бар для бонусов
    @FXML private VBox bonusBox;                 // всплывающая подсказка

    // поля для работы с данными
    private UserDao userDao;    // dao для пользователей
    private User currentUser;   // текущий пользователь

    // метод инициализации контроллера
    @FXML
    public void initialize() {
        userDao = new UserDao();
        int userId = UserSession.getUserId();
        if (userId > 0) {

            // загружаем данные пользователя из базы
            currentUser = userDao.findById(userId);
            if (currentUser != null) {
                fillFields(); // заполняем поля формы
            } else {
                showAlert("Ошибка", "Не удалось загрузить данные пользователя");
            }
        } else {

            // если пользователь не авторизован, перенаправляем на окно входа
            logout();
        }

        // поля изначально не редактируемые
        setEditable(false);
    }

    // метод заполняет поля формы данными из объекта currentUser
    private void fillFields() {
        lastNameField.setText(currentUser.getLastName());
        firstNameField.setText(currentUser.getFirstName());
        middleNameField.setText(currentUser.getMiddleName());
        phoneField.setText(currentUser.getPhone());
        emailField.setText(currentUser.getEmail());
    }

    // метод включает/отключает режим редактирования полей
    private void setEditable(boolean editable) {
        lastNameField.setEditable(editable);
        firstNameField.setEditable(editable);
        middleNameField.setEditable(editable);
        phoneField.setEditable(editable);
        emailField.setEditable(editable);
        saveButton.setDisable(!editable); // сохранить активна только в режиме редактирования
        editButton.setDisable(editable);  // кнопка редактирования отключается при редактировании
    }

    // обработчик кнопки "редактировать"
    @FXML
    private void editProfile() {
        setEditable(true);
    }

    // обработчик кнопки "сохранить"
    @FXML
    private void saveProfile() {

        // обновляем поля объекта currentUser из текстовых полей формы
        currentUser.setLastName(lastNameField.getText().trim());
        currentUser.setFirstName(firstNameField.getText().trim());
        currentUser.setMiddleName(middleNameField.getText().trim());
        currentUser.setPhone(phoneField.getText().trim());
        currentUser.setEmail(emailField.getText().trim());

        boolean updated = userDao.update(currentUser);
        if (updated) {
            setEditable(false); // выключаем режим редактирования
            showAlert("Успех", "Данные обновлены");
        } else {
            showAlert("Ошибка", "Не удалось сохранить изменения");
        }
    }

    // обработчик кнопки "сменить пароль"
    @FXML
    private void changePassword() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Смена пароля");
        dialog.setHeaderText("Введите новый пароль");
        dialog.setContentText("Новый пароль:");
        dialog.showAndWait().ifPresent(newPassword -> {
            if (newPassword.length() < 4) {
                showAlert("Ошибка", "Пароль должен быть не менее 4 символов");
                return;
            }
            String hash = PasswordUtils.hashPassword(newPassword);
            currentUser.setPasswordHash(hash);
            boolean updated = userDao.update(currentUser);
            if (updated) {
                showAlert("Успех", "Пароль изменен");
            } else {
                showAlert("Ошибка", "Не удалось сменить пароль");
            }
        });
    }

    // обработчик кнопки "выйти"
    @FXML
    private void logout() {
        UserSession.clear(); // очищаем сессию
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/client.Kholenko/login-view.fxml"));
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // метод для отображения информационного диалога
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}