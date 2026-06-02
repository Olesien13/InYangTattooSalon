package dao;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;

// dao для получения статистических данных

public class StatisticsDao {

    // доход компании по месяцам (только выполненные записи)
    public Map<String, Double> getMonthlyRevenue() {
        Map<String, Double> revenue = new HashMap<>();
        String sql = """
            SELECT 
                strftime('%Y-%m', appointment_date) as month,
                SUM(final_price) as total
            FROM appointments
            WHERE status = 'Выполнено'
            GROUP BY strftime('%Y-%m', appointment_date)
            ORDER BY month
        """;

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                revenue.put(rs.getString("month"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }

    // заработок каждого мастера с клиентов (только выполненные записи)
    public Map<String, Double> getMasterRevenue() {
        Map<String, Double> revenue = new HashMap<>();
        String sql = """
            SELECT 
                m.name as master_name,
                SUM(a.final_price) as total
            FROM appointments a
            JOIN masters m ON a.master_id = m.id
            WHERE a.status = 'Выполнено'
            GROUP BY m.id
            ORDER BY total DESC
        """;

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                revenue.put(rs.getString("master_name"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return revenue;
    }
}