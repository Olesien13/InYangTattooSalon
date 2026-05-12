import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import dao.DatabaseConnection;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        // Подключаемся к БД при запуске приложения
        DatabaseConnection.getConnection();

        // создаем загрузчик fxml, указывая путь к файлу интерфейса входа
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/client/main-view.fxml"));

        // загружаем fxml и создаем сцену
        Scene scene = new Scene(loader.load());
        stage.setTitle("Тату-салон Инь-Янь"); // устанавливаем заголовок окна
        stage.setScene(scene); // помещаем сцену в окно
        stage.show(); // показываем окно

        // При закрытии окна - закрываем соединение с БД
        stage.setOnCloseRequest(event -> {
            DatabaseConnection.closeConnection();
        });
    }
}