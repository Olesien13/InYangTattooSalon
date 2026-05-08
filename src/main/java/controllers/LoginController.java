package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import dao.UserDao;
import models.User;
import utils.*;
import java.util.Random;

// класс контроллера для окна входа
public class LoginController {

    // поля, которые связаны с элементами из fxml
    @FXML private TextField emailField;           // поле ввода email
    @FXML private PasswordField passwordField;    // поле ввода пароля
    @FXML private Label errorLabel;               // метка для показа ошибок
    @FXML private ToggleButton clientToggleBtn;   // кнопка выбора роли "Клиент"
    @FXML private ToggleButton adminToggleBtn;    // кнопка выбора роли "Администратор"

    private ToggleGroup groupRole;  // группа переключателей, чтобы одновременно была выбрана только одна роль
    private UserDao userDao;

    // инициализируем группу переключателей и устанавливаем выбор "Клиент" по умолчанию
    @FXML
    public void initialize() {
        userDao = new UserDao();  // инициализируем DAO
        groupRole = new ToggleGroup();              // создаем новую группу
        clientToggleBtn.setToggleGroup(groupRole);  // добавляем кнопку клиента в группу
        adminToggleBtn.setToggleGroup(groupRole);   // добавляем кнопку админа в группу
        clientToggleBtn.setSelected(true);          // по умолчанию выбран клиент
    }

    // обработчик нажатия на кнопку "Войти"
    @FXML
    private void loginButton(ActionEvent event) {

        // получаем введенный email, удаляя лишние пробелы по краям, и пароль
        String email = emailField.getText().trim();
        String password = passwordField.getText();

        // проверяем, что поля не пустые
        if (email.isEmpty() || password.isEmpty()) {
            showError("Введите email и пароль."); // вызываем метод показа ошибки
            return; // прерываем выполнение
        }

        // ищем пользователя по email
        User user = userDao.findByEmail(email);
        if (user == null) {
            showError("Пользователь не найден.");
            return;
        }

        // определяем, выбрана ли роль администратора и является ли пользователь админом
        boolean adminChosen = adminToggleBtn.isSelected();
        boolean userAdmin = "admin".equals(user.getRole());

        // если выбрана роль админа, но пользователь не админ - ошибка
        if (adminChosen && !userAdmin) {
            showError("Этот пользователь не администратор.");
            return;
        }

        // если выбрана роль клиента, но пользователь админ - тоже ошибка
        if (!adminChosen && userAdmin) {
            showError("Выбран вход как клиент, но это админ");
            return;
        }

        // проверяем пароль: сравниваем хеш введенного пароля с сохраненным хешем
        if (!PasswordUtils.checkPassword(password, user.getPasswordHash())) {
            showError("Неверный пароль.");
            return;
        }

        // вход успешен - сохраняем данные в сессию
        utils.UserSession.start(user.getId(), user.getEmail(), user.getRole());

        // определяем путь к fxml-файлу в зависимости от роли
        String fxmlPath = userAdmin ? "/admin/menu.fxml" : "/client/main-view.fxml";
        try {
            // загружаем нужную сцену
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load(); // корневой элемент новой сцены
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); // текущее окно
            stage.setScene(new Scene(root)); // устанавливаем новую сцену
            stage.setOnCloseRequest(e -> utils.UserSession.clear()); // при закрытии окна сбрасываем сессию
            stage.show();  // показываем окно
        } catch (Exception e) {
            e.printStackTrace(); // печатаем ошибку в консоль
            showError("Ошибка загрузки интерфейса: " + e.getMessage());
        }
    }

    // переход на окно регистрации по кнопке "РЕГИСТРАЦИЯ"
    @FXML
    private void goRegister(ActionEvent event) throws Exception {

        // загружаем fxml файл регистрации
        Parent root = FXMLLoader.load(getClass().getResource("/client/register-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    // восстановление пароля "Забыли пароль?"
    @FXML
    private void forgotPassword(MouseEvent event) {
        String email = emailField.getText().trim();
        if (email.isEmpty()) {
            showError("Введите email для восстановления.");
            return;
        }
        User user = userDao.findByEmail(email);
        if (user == null) {
            showError("Пользователь не найден.");
            return;
        }

        // Генерация случайного пароля
        String newPassword = generateRandomPassword();
        String newHash = PasswordUtils.hashPassword(newPassword);

        // Обновление пароля в базе данных
        user.setPasswordHash(newHash);
        boolean updated = userDao.update(user);

        if (updated) {
            // Отправка нового пароля на почту
            EmailUtils.sendPasswordResetEmail(email, newPassword);
            showError("Новый пароль отправлен на вашу почту.");
        } else {
            showError("Ошибка при смене пароля. Попробуйте позже.");
        }
    }

    // генератор случайного пароля
    private String generateRandomPassword() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789";

        StringBuilder password = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }

        return password.toString();
    }
    // метод для сообщения об ошибке в метке errorLabel
    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}