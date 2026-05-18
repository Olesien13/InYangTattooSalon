package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.ClientDao;
import dao.UserDao;
import models.Client;
import models.User;

public class ChangeClientController {

    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private ComboBox<String> serviceComboBox;
    @FXML private TextField totalSpentField;
    @FXML private TextField dateField;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private Label errorLabel;

    private ClientDao clientDao;
    private UserDao userDao;
    private Client currentClient;

    @FXML
    public void initialize() {
        clientDao = new ClientDao();
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

        // Список статусов для комбобокса (на русском)
        statusComboBox.getItems().addAll(
                "В обработке",
                "Подтверждено",
                "Выполнено",
                "Отменено"
        );
        statusComboBox.setEditable(false);
    }

    public void setClient(Client client) {
        this.currentClient = client;

        nameField.setText(client.getName());
        phoneField.setText(client.getPhone());
        emailField.setText(client.getEmail());
        serviceComboBox.setValue(client.getServiceName());
        totalSpentField.setText(String.valueOf(client.getTotalSpent()));
        dateField.setText(client.getRegistrationDate());
        dateField.setText(client.getDate());
        statusComboBox.setValue(client.getStatus());
    }

    @FXML
    private void updateClient() {
        if (nameField.getText().trim().isEmpty()) {
            showError("Введите ФИО клиента");
            return;
        }

        // Получаем пользователя
        User user = userDao.findById(currentClient.getId());
        if (user != null) {
            // Разделяем ФИО на части
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

        if (dateField.getText() != null && !dateField.getText().isEmpty() && statusComboBox.getValue() != null) {
            String newStatus = statusComboBox.getValue();
            System.out.println("Обновляем статус на: " + newStatus);

            clientDao.updateAppointmentStatus(currentClient.getId(), dateField.getText(), newStatus);

            if ("Отменено".equals(newStatus)) {
                clientDao.updateAppointmentPrice(currentClient.getId(), dateField.getText(), 0);
            } else {
                double price = getServicePrice(serviceComboBox.getValue());
                System.out.println("Цена услуги: " + price);
                clientDao.updateAppointmentPrice(currentClient.getId(), dateField.getText(), price);
            }
        }


        closeWindow();
    }


    // Вспомогательный метод для получения цены услуги
    private double getServicePrice(String serviceName) {
        if (serviceName == null) return 0;
        switch (serviceName) {
            case "Цветное тату": return 4000;
            case "Черно-белое тату": return 3000;
            case "Перекрытие": return 5000;
            case "Коррекция": return 2000;
            case "Сведение": return 6000;
            case "Эскиз": return 500;
            case "Консультация": return 0;
            default: return 0;
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