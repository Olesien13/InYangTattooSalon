package coursework.itms206.controllers.client.Kholenko;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import coursework.itms206.dao.UserDao;
import coursework.itms206.models.User;
import coursework.itms206.utils.UserSession;
import coursework.itms206.utils.PasswordUtils;
import javafx.scene.control.Tooltip;

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
                updateBonusInfo();

                // Создаём всплывающую подсказку
                Tooltip bonusTooltip = new Tooltip();
                bonusTooltip.setText(
                        "Бонусная система:\n" +
                                "• После 1 выполненной услуги – скидка 5%\n" +
                                "• После 5 выполненных услуг – скидка 10%\n" +
                                "• После 10 выполненных услуг – скидка 15%\n\n" +
                                "Скидка применяется к следующей услуге автоматически при оформлении записи."
                );
                bonusTooltip.setStyle(
                        "-fx-background-color: white; " +
                                "-fx-text-fill: #1A1A1A; " +
                                "-fx-font-family: 'Open Sans'; " +
                                "-fx-font-size: 12px; " +
                                "-fx-border-color: #771011; " +
                                "-fx-border-radius: 8px; " +
                                "-fx-background-radius: 8px; " +
                                "-fx-padding: 8px;"
                );
                Tooltip.install(bonusBox, bonusTooltip);
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

    private void updateBonusInfo() {
        if (currentUser == null) return;
        int completed = currentUser.getCompletedServicesCount();
        int discount = currentUser.getDiscount();
        discountLabel.setText("Ваша скидка: " + discount + "%");
        int nextTarget;
        int nextDiscount;
        if (completed < 1) {
            nextTarget = 1;
            nextDiscount = 5;
        } else if (completed < 5) {
            nextTarget = 5;
            nextDiscount = 10;
        } else if (completed < 10) {
            nextTarget = 10;
            nextDiscount = 15;
        } else {
            nextTarget = completed;
            nextDiscount = discount;
        }
        nextLevelLabel.setText("Следующий уровень: " + nextDiscount + "% после " + nextTarget + " посещений");
        double progress = Math.min(1.0, (double) completed / (completed < 1 ? 1 : (completed < 5 ? 5 : (completed < 10 ? 10 : 1))));
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