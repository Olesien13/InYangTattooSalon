package dao;

import models.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDao {

    // Получить все услуги
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
                s.setImagePath(rs.getString("image_path"));
                services.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    // Найти услугу по id
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

    // СОЗДАТЬ новую услугу (INSERT)
    public boolean create(Service service) {
        String sql = "INSERT INTO services (name, duration_minutes, price, image_path) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, service.getName());
            pstmt.setInt(2, service.getDurationMinutes());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setString(4, service.getImagePath());
            int affected = pstmt.executeUpdate();
            if (affected > 0) {
                ResultSet rs = pstmt.getGeneratedKeys();
                if (rs.next()) service.setId(rs.getInt(1));
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // ОБНОВИТЬ услугу (UPDATE)
    public boolean update(Service service) {
        String sql = "UPDATE services SET name = ?, duration_minutes = ?, price = ?, image_path = ? WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, service.getName());
            pstmt.setInt(2, service.getDurationMinutes());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setString(4, service.getImagePath());
            pstmt.setInt(5, service.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // УДАЛИТЬ услугу (DELETE) – сначала удаляем связи из master_services, потом саму услугу
    public boolean delete(int id) {
        // Удаляем связи с мастерами
        String deleteLinks = "DELETE FROM master_services WHERE service_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deleteLinks)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Удаляем саму услугу
        String sql = "DELETE FROM services WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}