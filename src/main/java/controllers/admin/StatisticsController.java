package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import dao.StatisticsDao;

import java.io.IOException;
import java.util.Map;

public class StatisticsController {

    @FXML private PieChart revenueChart;
    @FXML private BarChart<String, Number> masterRevenueBarChart;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        loadPieChart();
        loadBarChart();
    }



    private void loadPieChart() {
        StatisticsDao dao = new StatisticsDao();
        Map<String, Double> monthlyRevenue = dao.getMonthlyRevenue();

        revenueChart.getData().clear();

        for (Map.Entry<String, Double> entry : monthlyRevenue.entrySet()) {
            String month = entry.getKey();
            double amount = entry.getValue();
            // Формируем подпись: месяц и сумма
            String label = month + " - " + Math.round(amount) + " руб";
            PieChart.Data data = new PieChart.Data(label, amount);
            revenueChart.getData().add(data);
        }

        // Показываем легенду
        revenueChart.setLegendVisible(true);

        // Добавляем подписи прямо на сегменты (для больших сегментов)
        for (PieChart.Data data : revenueChart.getData()) {
            data.setName(data.getName());
        }
    }

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

    @FXML
    private void goBack() {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/admin/menu.fxml"));
            Stage stage = (Stage) backButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("Главное меню");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}