package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import models.Service;
import dao.ServiceDao;
import utils.CurrentAppointment;

import java.util.List;

public class MainController {

    // поле для контейнера карточек услуг из fxml
    @FXML private TilePane servicesTilePane;

    // метод инициализации
    @FXML
    public void initialize() {

        // создаем объект dao для доступа к услугам
        ServiceDao serviceDao = new ServiceDao();

        // получаем список всех услуг из базы данных
        List<Service> services = serviceDao.getAll();

        // очищаем контейнер перед добавлением
        servicesTilePane.getChildren().clear();

        // динамически создаем карточки для каждой услуги
        for (Service service : services) {
            StackPane card = new StackPane(); // создаем корневой элемент карточки для наложения элементов
            card.getStyleClass().add("card"); // добавляем css-класс для стилизации
            card.setPrefWidth(120); // задаем фиксированную ширину карточки
            card.setPrefHeight(120);// задаем фиксированную высоту карточки

            // создаем элемент для отображения иконки услуги
            ImageView imageView = new ImageView();
            imageView.setFitWidth(120);
            imageView.setFitHeight(120);
            imageView.getStyleClass().add("card-image");

            // получаем путь к изображению из модели услуги
            String imagePath = service.getImagePath();
            if (imagePath != null && !imagePath.isEmpty()) {
                try {

                    // загружаем изображение из ресурсов проекта
                    Image img = new Image(getClass().getResourceAsStream(imagePath));
                    imageView.setImage(img);
                } catch (Exception e) {

                    // если загрузка не удалась, оставляем пустую иконку
                }
            }
            // добавляем imageview в карточку
            card.getChildren().add(imageView);

            // создаем метку для названия услуги
            Label nameLabel = new Label(service.getName());
            nameLabel.getStyleClass().add("card-header");
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-size: 14px;");
            nameLabel.setWrapText(true);
            nameLabel.setTextAlignment(TextAlignment.CENTER);
            card.getChildren().add(nameLabel);

            // создаем метку для цены услуги
            Label priceLabel = new Label("От " + (int) service.getPrice() + " ₽");
            priceLabel.getStyleClass().add("card-sena");
            priceLabel.setStyle("-fx-text-fill: #ff3366; -fx-font-size: 13px;");
            card.getChildren().add(priceLabel);

            // добавляем обработчик клика по карточке
            card.setOnMouseClicked(event -> onServiceSelected(service));

            // добавляем созданную карточку в контейнер
            servicesTilePane.getChildren().add(card);
        }
    }

    // обработчик выбора услуги: сохраняет услугу и открывает окно выбора мастера
    private void onServiceSelected(Service service) {

        // сохраняем выбранную услугу в глобальное хранилище текущей записи
        CurrentAppointment.setService(service);

        // вызываем метод для открытия следующего окна
        openMasterSelectionWindow();
    }

    // метод для открытия окна выбора мастера
    private void openMasterSelectionWindow() {
        try {

            // загружаем fxml-файл окна выбора мастера
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/master-selection-view.fxml"));
            Parent root = loader.load();

            // получаем текущее окно (stage) из любого элемента сцены
            Stage currentStage = (Stage) servicesTilePane.getScene().getWindow();

            // устанавливаем новую сцену в текущем окне
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (Exception e) {

            // печатаем стек ошибки в консоль
            e.printStackTrace();
        }
    }
}