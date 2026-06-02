package controllers.client.Kholenko;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import dao.MasterDao;
import models.Master;
import models.Service;
import utils.CurrentAppointment;

import java.util.List;

public class MasterSelectionController {

    @FXML private Label titleLabel;        // заголовок окна
    @FXML private ToggleButton alexBtn;    // одна из статичных кнопок, нужна для получения родительского контейнера
    @FXML private Label descriptionLabel;  // описание выбранного мастера
    @FXML private ImageView masterPhoto;   // аватарка мастера
    @FXML private VBox portfolioVBox;      // контейнер для фотографий портфолио
    @FXML private Button backButton;       // кнопка "назад"
    @FXML private Button nextButton;       // кнопка "далее"

    @FXML
    private ToggleGroup masterGroup;       // группа переключателей
    private List<Master> masters;          // список мастеров, подходящих для выбранной услуги
    private Service currentService;        // выбранная услуга (передана из главного окна)

    // метод инициализации
    @FXML
    public void initialize() {

        // получаем ранее выбранную услугу из глобального хранилища
        currentService = CurrentAppointment.getService();
        if (currentService == null) {
            try {
                goBack();
            } catch (Exception e) {
                e.printStackTrace();
            }
            return;
        }

        // устанавливаем заголовок окна в зависимости от выбранной услуги
        titleLabel.setText("Мастера " + currentService.getName());

        // загружаем мастеров, которые могут выполнить выбранную услугу, через таблицу master_services
        MasterDao masterDao = new MasterDao();
        masters = masterDao.getByServiceId(currentService.getId());

        // находим контейнер, в котором лежат статичные кнопки
        VBox buttonsContainer = (VBox) alexBtn.getParent();
        buttonsContainer.getChildren().clear();   // удаляем статичные кнопки
        masterGroup = new ToggleGroup();

        // динамически создаем кнопки для каждого мастера из списка
        for (Master master : masters) {

            // создаем кнопку-переключатель
            ToggleButton btn = new ToggleButton();
            btn.setToggleGroup(masterGroup);
            btn.setUserData(master); // привязываем объект мастера к кнопке
            btn.getStyleClass().add("masters-button");
            btn.setPrefHeight(58);
            btn.setPrefWidth(133);
            btn.setWrapText(true);

            // создаем вертикальный контейнер для имени и специализации
            VBox content = new VBox(2); // spacing 2px
            content.setAlignment(javafx.geometry.Pos.CENTER);
            content.getStyleClass().add("master-button-content");

            // метка с именем мастера
            Label nameLabel = new Label(master.getName());
            nameLabel.setStyle("-fx-font-size: 13px;");
            nameLabel.setAlignment(javafx.geometry.Pos.CENTER);
            nameLabel.getStyleClass().add("master-name");

            // метка со специализацией мастера
            Label specLabel = new Label(master.getSpecialization());
            specLabel.getStyleClass().add("master-specialization");
            specLabel.setAlignment(javafx.geometry.Pos.CENTER);

            content.getChildren().addAll(nameLabel, specLabel);

            // помещаем вертикальный контейнер в кнопку как графику
            btn.setGraphic(content);
            btn.setContentDisplay(javafx.scene.control.ContentDisplay.GRAPHIC_ONLY);
            btn.setText(null); // убираем стандартный текст кнопки

            // добавляем отступ снизу для кнопки
            VBox.setMargin(btn, new Insets(0, 0, 10, 0));
            buttonsContainer.getChildren().add(btn);
        }

        // слушатель выбора мастера в группе
        masterGroup.selectedToggleProperty().addListener((obs, old, newVal) -> {
            if (newVal != null) {
                Master selected = (Master) newVal.getUserData(); // получаем выбранного мастера
                updateMasterInfo(selected);                      // обновляем правую панель
                nextButton.setDisable(false);                    // активируем кнопку "далее"
            }
        });

        // если мастера есть, выбираем первого по умолчанию
        if (!buttonsContainer.getChildren().isEmpty()) {
            ((ToggleButton) buttonsContainer.getChildren().get(0)).setSelected(true);
        } else {

            // если мастеров нет, кнопка "далее" недоступна, показываем ошибку
            nextButton.setDisable(true);
            showError("Нет мастеров для этой услуги");
        }

        // привязываем обработчики к кнопкам навигации
        backButton.setOnAction(e -> goBack());
        nextButton.setOnAction(e -> goToCalendar());
    }

    // обновляет правую панель: описание, аватарку и портфолио выбранного мастера
    private void updateMasterInfo(Master master) {

        // устанавливаем текст описания мастера
        descriptionLabel.setText(master.getDescription()) ;

        // загружаем аватарку мастера
        String photo_url = master.getAvatarPath();
        if (photo_url != null && !photo_url.isEmpty()) {
            try {
                Image img = new Image(getClass().getResourceAsStream(photo_url));
                masterPhoto.setImage(img);
            } catch (Exception e) {
                masterPhoto.setImage(null);
            }
        } else {
            masterPhoto.setImage(null);
        }

        // загружаем портфолио – только для текущей услуги
        portfolioVBox.getChildren().clear();
        MasterDao masterDao = new MasterDao();
        int currentServiceId = currentService.getId(); // получаем id выбранной услуги
        List<String> portfolioImages = masterDao.getPortfolioImages(master.getId(), currentServiceId);

        // количество изображений в строке
        int cols = 3;
        for (int i = 0; i < portfolioImages.size(); i += cols) {

            // создаем горизонтальную строку для трех фотографий
            HBox row = new HBox(15); // расстояние между фото 15px
            row.setAlignment(Pos.CENTER);
            for (int j = 0; j < cols && i + j < portfolioImages.size(); j++) {
                String imgPath = portfolioImages.get(i + j);
                try {
                    ImageView iv = new ImageView();
                    iv.setFitWidth(100);
                    iv.setFitHeight(100);
                    iv.setImage(new Image(getClass().getResourceAsStream(imgPath)));

                    // оборачиваем imageview в stackpane для стилизации
                    StackPane sp = new StackPane(iv);
                    sp.getStyleClass().add("card-referens");
                    row.getChildren().add(sp);
                } catch (Exception e) {

                    // если картинка не загрузилась, просто пропускаем
                }
            }
            portfolioVBox.getChildren().add(row);
        }
    }

    // переход на окно календаря
    @FXML
    private void goToCalendar() {

        // получаем выбранную кнопку из группы
        ToggleButton selected = (ToggleButton) masterGroup.getSelectedToggle();
        if (selected != null) {
            Master master = (Master) selected.getUserData(); // извлекаем мастера

            // сохраняем выбранного мастера в глобальное хранилище
            CurrentAppointment.setMaster(master);
            try {
                // загружаем fxml календаря
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/client.Kholenko/calendar-view.fxml"));
                Parent root = loader.load();

                // получаем текущее окно (stage) по кнопке "далее"
                Stage currentStage = (Stage) nextButton.getScene().getWindow();
                currentStage.setScene(new Scene(root));
                currentStage.show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // возврат на главное окно
    @FXML
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/client.Kholenko/main-view.fxml"));
            Parent root = loader.load();
            Stage currentStage = (Stage) backButton.getScene().getWindow();
            currentStage.setScene(new Scene(root));
            currentStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // показывает всплывающее диалоговое окно с сообщением об ошибке
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        alert.showAndWait();
    }
}