package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class StatisticsDao {

    public Map<String, Double> getMonthlyRevenue() {
        Map<String, Double> revenue = new HashMap<>();
        String sql = """
        SELECT 
            strftime('%Y-%m', appointment_date) as month,
            SUM(final_price) as total
        FROM appointments
        WHERE status = 'Выполнено' 
            AND strftime('%Y-%m', appointment_date) IN ('2026-03', '2026-04', '2026-05')
        GROUP BY strftime('%Y-%m', appointment_date)
        ORDER BY month
    """;

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                String month = rs.getString("month");
                double total = rs.getDouble("total");
                revenue.put(month, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }

    public Map<String, Double> getYearlyRevenue(int year) {
        Map<String, Double> revenue = new HashMap<>();
        String sql = """
            SELECT 
                strftime('%m', appointment_date) as month,
                SUM(final_price) as total
            FROM appointments
            WHERE status = 'Выполнено' AND strftime('%Y', appointment_date) = ?
            GROUP BY strftime('%m', appointment_date)
            ORDER BY month
        """;

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, String.valueOf(year));
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String month = rs.getString("month");
                double total = rs.getDouble("total");
                revenue.put(month, total);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }
}
