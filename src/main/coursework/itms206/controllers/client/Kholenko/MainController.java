package coursework.itms206.controllers.client.Kholenko;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import coursework.itms206.models.Service;
import coursework.itms206.dao.ServiceDao;
import coursework.itms206.utils.CurrentAppointment;

import java.util.List;

public class MainController {

    @FXML private TilePane servicesTilePane;
    @FXML
    private TabPane mainTabPane;

    @FXML
    public void initialize() {

        // Загружаем услуги из БД
        ServiceDao serviceDao = new ServiceDao();
        List<Service> services = serviceDao.getAll();

        if (services == null || services.isEmpty()) {
            System.err.println("В таблице services нет записей. Добавьте услуги в базу данных.");
            return;
        }

        // Очищаем и добавляем карточки
        servicesTilePane.getChildren().clear();
        for (Service service : services) {
            StackPane card = new StackPane();
            card.getStyleClass().add("card");
            card.setPrefWidth(120);
            card.setPrefHeight(120);

            // Изображение услуги
            ImageView imageView = new ImageView();
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.getStyleClass().add("card-image");
            String imagePath = service.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {
                    Image img = new Image(getClass().getResourceAsStream(imagePath));
                    imageView.setImage(img);
                } catch (Exception e) {
                    System.err.println("Не удалось загрузить изображение: " + imagePath);
                    e.printStackTrace();
                }
            }
            card.getChildren().add(imageView);

            // Название
            Label nameLabel = new Label(service.getName());
            nameLabel.getStyleClass().add("card-header");
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            nameLabel.setWrapText(true);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            card.getChildren().add(nameLabel);

            // Цена
            Label priceLabel = new Label("От " + (int) service.getPrice() + " Р");
            priceLabel.getStyleClass().add("card-sena");
            priceLabel.setStyle("-fx-text-fill: #ff3366; -fx-font-size: 13px;");
            card.getChildren().add(priceLabel);

            card.setOnMouseClicked(event -> onServiceSelected(service));
            servicesTilePane.getChildren().add(card);
        }

        System.out.println("Карточек добавлено: " + servicesTilePane.getChildren().size());
    }

    private void onServiceSelected(Service service) {
        CurrentAppointment.setService(service);
        openMasterSelectionWindow();
    }

    private void openMasterSelectionWindow() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client.Kholenko/master-selection-view.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) servicesTilePane.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void setActiveTab(String tabText) {
        for (Tab tab : mainTabPane.getTabs()) {
            if (tab.getText().equals(tabText)) {
                mainTabPane.getSelectionModel().select(tab);
                break;
            }
        }
    }
}