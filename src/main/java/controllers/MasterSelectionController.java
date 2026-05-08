package controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dao.MasterDao;
import models.Master;
import utils.UserSession;

import java.util.List;

public class MasterSelectionController {

    @FXML private VBox mastersContainer;  // контейнер для списка мастеров
    @FXML private Label errorLabel;

    private MasterDao masterDao;
    private Master selectedMaster;  // выбранный мастер

    @FXML
    public void initialize() {
        masterDao = new MasterDao();
        loadMasters();  // загружаем список мастеров
    }

    // Загрузка мастеров из БД
    private void loadMasters() {
        mastersContainer.getChildren().clear();

        List<Master> masters = masterDao.getAll();  // ← вместо DatabaseMock

        if (masters.isEmpty()) {
            showError("Нет доступных мастеров");
            return;
        }

        for (Master master : masters) {
            Button masterButton = new Button(master.getName() + " - " + master.getSpecialization());
            masterButton.setMaxWidth(Double.MAX_VALUE);
            masterButton.setStyle("-fx-font-size: 14px; -fx-padding: 10;");

            // Обработчик выбора мастера
            masterButton.setOnAction(event -> selectMaster(master, event));

            mastersContainer.getChildren().add(masterButton);
        }
    }

    // Выбор мастера и переход к выбору услуги
    private void selectMaster(Master master, ActionEvent event) {
        selectedMaster = master;

        // Сохраняем выбранного мастера в сессию (или передаем через параметр)
        UserSession.setSelectedMasterId(master.getId());

        try {
            Parent root = FXMLLoader.load(getClass().getResource("/client/service-selection.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка перехода: " + e.getMessage());
        }
    }

    // Кнопка "Назад"
    @FXML
    private void goBack(ActionEvent event) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/client/main-view.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
}