package controllers.admin.Shokina;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.ClientDao;
import dao.UserDao;
import models.Client;
import models.User;

// контроллер окна редактирования клиента

public class ChangeClientController {

    @FXML private TextField nameField;                    // поле фио
    @FXML private TextField phoneField;                   // поле телефон
    @FXML private TextField emailField;                   // поле почта
    @FXML private ComboBox<String> serviceComboBox;       // выбор услуги
    @FXML private TextField dateField;                    // дата записи
    @FXML private ComboBox<String> statusComboBox;        // статус записи
    @FXML private Label errorLabel;                       // метка ошибок
    @FXML private TextField priceField;                   // поле цена

    private ClientDao clientDao;       // dao для работы с клиентами
    private UserDao userDao;           // dao для работы с пользователями
    private Client currentClient;      // редактируемый клиент

    @FXML
    public void initialize() {
        clientDao = new ClientDao();
        userDao = new UserDao();

        // список услуг
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

        // список статусов
        statusComboBox.getItems().addAll(
                "В обработке",
                "Подтверждено",
                "Выполнено",
                "Отменено"
        );
        statusComboBox.setEditable(false);
    }

    // передача данных из ClientsController
    public void setClient(Client client) {
        this.currentClient = client;

        nameField.setText(client.getName());
        phoneField.setText(client.getPhone());
        emailField.setText(client.getEmail());
        serviceComboBox.setValue(client.getServiceName());
        priceField.setText(String.valueOf(client.getTotalSpent()));
        dateField.setText(client.getDate());
        statusComboBox.setValue(client.getStatus());
    }

    // сохранение изменений
    @FXML
    private void updateClient() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите ФИО клиента");
            return;
        }

        // обновление данных пользователя
        User user = userDao.findById(currentClient.getId());
        if (user != null) {
            String[] nameParts = nameField.getText().trim().split(" ", 3);
            String lastName = nameParts[0];
            String firstName = nameParts.length > 1 ? nameParts[1] : "";
            String middleName = nameParts.length > 2 ? nameParts[2] : "";

            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setMiddleName(middleName);
            user.setPhone(phoneField.getText().trim());
            user.setEmail(emailField.getText().trim());

            userDao.update(user);
        }

        // получение новой цены
        double newPrice = 0;
        try {
            newPrice = Double.parseDouble(priceField.getText().trim());
        } catch (NumberFormatException e) {
            showError("Введите корректную цену");
            return;
        }

        // обновление статуса и цены записи
        if (dateField.getText() != null && !dateField.getText().isEmpty() && statusComboBox.getValue() != null) {
            String newStatus = statusComboBox.getValue();

            clientDao.updateAppointmentStatus(currentClient.getId(), dateField.getText(), newStatus);

            if ("Отменено".equals(newStatus)) {
                clientDao.updateAppointmentPrice(currentClient.getId(), dateField.getText(), 0);
            } else {
                clientDao.updateAppointmentPrice(currentClient.getId(), dateField.getText(), newPrice);
            }
        }

        closeWindow();
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