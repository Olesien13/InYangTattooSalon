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
            SELECT s.id, s.name, s.duration_minutes, s.price, 
                   m.id as master_id, m.name as master_name
            FROM services s
            JOIN master_services ms ON s.id = ms.service_id
            JOIN masters m ON ms.master_id = m.id
            WHERE m.is_active = 1
        """;

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
        String sql = """
            SELECT s.id, s.name, s.duration_minutes, s.price, 
                   m.id as master_id, m.name as master_name
            FROM services s
            JOIN master_services ms ON s.id = ms.service_id
            JOIN masters m ON ms.master_id = m.id
            WHERE s.id = ?
        """;

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

    // Создать новую услугу и связать с мастером
    public boolean create(Service service) {
        String serviceSql = "INSERT INTO services (name, duration_minutes, price) VALUES (?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(serviceSql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, service.getName());
            pstmt.setInt(2, service.getDurationMinutes());
            pstmt.setDouble(3, service.getPrice());

            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                int serviceId = rs.getInt(1);
                // Связываем с мастером
                return linkWithMaster(serviceId, service.getMasterId());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Связать услугу с мастером
    private boolean linkWithMaster(int serviceId, int masterId) {
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

    // Обновить услугу
    public boolean update(Service service) {
        String sql = "UPDATE services SET name=?, duration_minutes=?, price=? WHERE id=?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, service.getName());
            pstmt.setInt(2, service.getDurationMinutes());
            pstmt.setDouble(3, service.getPrice());
            pstmt.setInt(4, service.getId());

            boolean updated = pstmt.executeUpdate() > 0;

            if (updated) {
                // Обновляем связь с мастером
                updateMasterLink(service.getId(), service.getMasterId());
            }
            return updated;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Обновить связь услуги с мастером
    private void updateMasterLink(int serviceId, int masterId) {
        String deleteSql = "DELETE FROM master_services WHERE service_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deleteSql)) {
            pstmt.setInt(1, serviceId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        linkWithMaster(serviceId, masterId);
    }

    // Удалить услугу
    public boolean delete(int id) {
        // Сначала удаляем связи
        String deleteLinks = "DELETE FROM master_services WHERE service_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deleteLinks)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Затем удаляем услугу
        String sql = "DELETE FROM services WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Service extractService(ResultSet rs) throws SQLException {
        Service service = new Service();
        service.setId(rs.getInt("id"));
        service.setName(rs.getString("name"));
        service.setDurationMinutes(rs.getInt("duration_minutes"));
        service.setPrice(rs.getDouble("price"));
        service.setMasterId(rs.getInt("master_id"));
        service.setMasterName(rs.getString("master_name"));
        return service;
    }
}