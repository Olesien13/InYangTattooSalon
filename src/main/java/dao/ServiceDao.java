package dao;

import models.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDao {

    public List<Service> getAll() {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT id, name, duration_minutes, price, image_path FROM services";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setDurationMinutes(rs.getInt("duration_minutes"));
                s.setPrice(rs.getDouble("price"));
                s.setImagePath(rs.getString("image_path"));   // читаем путь к иконке
                services.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    public Service findById(int id) {
        String sql = "SELECT id, name, duration_minutes, price, image_path FROM services WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setDurationMinutes(rs.getInt("duration_minutes"));
                s.setPrice(rs.getDouble("price"));
                s.setImagePath(rs.getString("image_path"));
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}