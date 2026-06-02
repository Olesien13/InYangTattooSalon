package controllers.admin;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import dao.ClientDao;
import dao.DatabaseConnection;
import models.Client;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// контроллер окна отчёта по расходникам клиента

public class ClientConsumablesReportController {

    @FXML private Label clientNameLabel;                              // имя клиента
    @FXML private ComboBox<String> appointmentComboBox;               // выбор записи
    @FXML private TableView<Map<String, Object>> consumablesTable;    // таблица расходников
    @FXML private TableColumn<Map<String, Object>, String> colConsumableName;
    @FXML private TableColumn<Map<String, Object>, Double> colUsedQuantity;
    @FXML private TableColumn<Map<String, Object>, Double> colTotalPrice;
    @FXML private Label totalLabel;                                   // общая стоимость
    @FXML private Button closeButton;                                 // кнопка закрытия

    private ClientDao clientDao;
    private Client client;
    private List<Integer> appointmentIds = new ArrayList<>();          // список id записей
    private List<String> appointmentDisplay = new ArrayList<>();      // список для отображения

    @FXML
    public void initialize() {
        clientDao = new ClientDao();

        // настройка колонок таблицы
        colConsumableName.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(
                        (String) cellData.getValue().get("consumable_name")
                )
        );
        colUsedQuantity.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(
                        (Double) cellData.getValue().get("total_quantity")
                ).asObject()
        );
        colTotalPrice.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleDoubleProperty(
                        (Double) cellData.getValue().get("total_price")
                ).asObject()
        );

        // обработчик выбора записи в combobox
        appointmentComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                int selectedIndex = appointmentComboBox.getSelectionModel().getSelectedIndex();
                if (selectedIndex >= 0 && selectedIndex < appointmentIds.size()) {
                    loadData(appointmentIds.get(selectedIndex));
                }
            }
        });
    }

    // установка клиента и загрузка его записей
    public void setClient(Client client) {
        this.client = client;
        clientNameLabel.setText("Клиент: " + client.getName());
        loadAppointments();
    }

    // загрузка списка записей клиента
    private void loadAppointments() {
        appointmentIds.clear();
        appointmentDisplay.clear();
        appointmentComboBox.getItems().clear();

        String sql = """
            SELECT a.id, s.name as service_name, a.appointment_date, a.status
            FROM appointments a
            JOIN services s ON a.service_id = s.id
            WHERE a.user_id = ?
            ORDER BY a.appointment_date DESC
        """;

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, client.getId());
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                int id = rs.getInt("id");
                String serviceName = rs.getString("service_name");
                String date = rs.getString("appointment_date");
                String status = rs.getString("status");

                String display = serviceName + " | " + date + " | " + status;
                if ("Отменено".equals(status)) {
                    display = serviceName + " | " + date + " | " + status + " (расходники не считаются)";
                }

                appointmentIds.add(id);
                appointmentDisplay.add(display);
                appointmentComboBox.getItems().add(display);
            }

            if (!appointmentIds.isEmpty()) {
                appointmentComboBox.getSelectionModel().selectFirst();
                loadData(appointmentIds.get(0));
            } else {
                consumablesTable.getItems().clear();
                totalLabel.setText("У клиента нет записей");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // загрузка расходников для выбранной записи
    private void loadData(int appointmentId) {
        List<Map<String, Object>> consumables = clientDao.getClientConsumables(appointmentId);
        consumablesTable.getItems().setAll(consumables);

        double total = 0;
        for (Map<String, Object> row : consumables) {
            total += (Double) row.get("total_price");
        }
        totalLabel.setText("Общая стоимость расходников: " + total + " руб");
    }

    // закрытие окна
    @FXML
    private void closeWindow() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}