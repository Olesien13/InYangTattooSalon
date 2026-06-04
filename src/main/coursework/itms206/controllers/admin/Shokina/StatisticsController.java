package coursework.itms206.controllers.admin.Shokina;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import coursework.itms206.dao.StatisticsDao;
import java.io.IOException;
import java.util.Map;

// контроллер окна статистики (доход компании и заработок мастеров)

public class StatisticsController {

    @FXML private PieChart revenueChart;                 // круговая диаграмма дохода по месяцам
    @FXML private BarChart<String, Number> masterRevenueBarChart; // столбчатая диаграмма заработка мастеров
    @FXML private Button backButton;                     // кнопка возврата в меню

    @FXML
    public void initialize() {
        loadPieChart();   // загрузка круговой диаграммы
        loadBarChart();   // загрузка столбчатой диаграммы
    }

    // загрузка данных для круговой диаграммы (доход по месяцам)
    private void loadPieChart() {
        StatisticsDao dao = new StatisticsDao();
        Map<String, Double> monthlyRevenue = dao.getMonthlyRevenue();

        revenueChart.getData().clear();

        for (Map.Entry<String, Double> entry : monthlyRevenue.entrySet()) {
            String month = entry.getKey();
            double amount = entry.getValue();
            String label = month + " - " + Math.round(amount) + " руб";
            PieChart.Data data = new PieChart.Data(label, amount);
            revenueChart.getData().add(data);
        }
    }

    // загрузка данных для столбчатой диаграммы (заработок мастеров)
    private void loadBarChart() {
        StatisticsDao dao = new StatisticsDao();
        Map<String, Double> masterRevenue = dao.getMasterRevenue();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Заработок с клиентов (руб)");

        for (Map.Entry<String, Double> entry : masterRevenue.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        masterRevenueBarChart.getData().clear();
        masterRevenueBarChart.getData().add(series);
    }

    // возврат в главное меню администратора
    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/admin.Shokina/menu.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Главное меню");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}