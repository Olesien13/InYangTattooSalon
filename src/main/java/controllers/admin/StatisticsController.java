package controllers.admin;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import dao.StatisticsDao;

import java.io.IOException;
import java.util.Map;

public class StatisticsController {

    @FXML private LineChart<String, Number> revenueChart;
    @FXML private CategoryAxis xAxis;
    @FXML private NumberAxis yAxis;
    @FXML private Button backButton;

    @FXML
    public void initialize() {
        loadChartData();
    }

    private void loadChartData() {
        StatisticsDao dao = new StatisticsDao();
        Map<String, Double> monthlyRevenue = dao.getMonthlyRevenue();

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Доход");

        for (Map.Entry<String, Double> entry : monthlyRevenue.entrySet()) {
            series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()));
        }

        revenueChart.getData().clear();
        revenueChart.getData().add(series);
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