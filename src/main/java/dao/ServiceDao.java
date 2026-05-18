package dao;

import models.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceDao {

    // Получить все услуги с именами мастеров
    public List<Service> getAll() {
        List<Service> services = new ArrayList<>();
        String sql = """
            SELECT s.id, s.name, s.duration_minutes, s.price, s.image_path,
                   GROUP_CONCAT(m.name, ', ') as master_names
            FROM services s
            LEFT JOIN master_services ms ON s.id = ms.service_id
            LEFT JOIN masters m ON ms.master_id = m.id
            GROUP BY s.id
            ORDER BY s.id
        """;

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Service s = new Service();
                s.setId(rs.getInt("id"));
                s.setName(rs.getString("name"));
                s.setDurationMinutes(rs.getInt("duration_minutes"));
                s.setPrice(rs.getDouble("price"));
                s.setImagePath(rs.getString("image_path"));
                s.setMasterName(rs.getString("master_names")); // ← Добавляем мастеров
                services.add(s);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return services;
    }

    // Найти услугу по id (с мастером)
    public Service findById(int id) {
        String sql = """
            SELECT s.id, s.name, s.duration_minutes, s.price, s.image_path,
                   GROUP_CONCAT(m.name, ', ') as master_names
            FROM services s
            LEFT JOIN master_services ms ON s.id = ms.service_id
            LEFT JOIN masters m ON ms.master_id = m.id
            WHERE s.id = ?
            GROUP BY s.id
        """;

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
                s.setMasterName(rs.getString("master_names"));
                return s;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // СОЗДАТЬ новую услугу (без связи с мастером – связь добавляется отдельно)
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

    // Создать услугу и связать с мастером
    public boolean createWithMaster(Service service, int masterId) {
        boolean created = create(service);
        if (created && masterId > 0) {
            return linkWithMaster(service.getId(), masterId);
        }
        return created;
    }

    // Связать услугу с мастером
    public boolean linkWithMaster(int serviceId, int masterId) {
        String sql = "INSERT INTO master_services (master_id, service_id) VALUES (?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            pstmt.setInt(2, serviceId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Обновить связи услуги с мастерами (удалить старые и добавить новые)
    public boolean updateMasterLinks(int serviceId, List<Integer> masterIds) {
        // Удалить старые связи
        String deleteSql = "DELETE FROM master_services WHERE service_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deleteSql)) {
            pstmt.setInt(1, serviceId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Добавить новые связи
        boolean allSuccess = true;
        for (int masterId : masterIds) {
            allSuccess = linkWithMaster(serviceId, masterId) && allSuccess;
        }
        return allSuccess;
    }

    // ОБНОВИТЬ услугу
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

    // УДАЛИТЬ услугу – сначала удаляем связи из master_services, потом саму услугу
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