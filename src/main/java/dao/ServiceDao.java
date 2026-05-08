package dao;

import models.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDao {

    // Получить все услуги
    public List<Service> getAll() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM services";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                services.add(extractService(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    // Найти услугу по id
    public Service findById(int id) {
        String sql = "SELECT * FROM services WHERE id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractService(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private Service extractService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getInt("id"));
        service.setName(rs.getString("name"));
        service.setDurationMinutes(rs.getInt("duration_minutes"));
        service.setPrice(rs.getDouble("price"));
        return service;
    }
}