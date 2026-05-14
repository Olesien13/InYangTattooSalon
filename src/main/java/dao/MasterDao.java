package dao;

import models.Master;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MasterDao {

    // Получить всех активных мастеров
    public List<Master> getAll() {
        List<Master> masters = new ArrayList<>();
        String sql = "SELECT id, name, phone, specialization, description, rating, hire_date, position_id, is_active, photo_url FROM masters WHERE is_active = 1";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                masters.add(extractMaster(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return masters;
    }

    // Найти мастера по id
    public Master findById(int id) {
        String sql = "SELECT id, name, phone, specialization, description, rating, hire_date, position_id, is_active, photo_url FROM masters WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractMaster(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Получить мастеров, выполняющих данную услугу (через master_services)
    public List<Master> getByServiceId(int serviceId) {
        List<Master> masters = new ArrayList<>();
        String sql = "SELECT m.id, m.name, m.phone, m.specialization, m.description, m.rating, m.hire_date, m.position_id, m.is_active, m.photo_url " +
                "FROM masters m " +
                "JOIN master_services ms ON m.id = ms.master_id " +
                "WHERE ms.service_id = ? AND m.is_active = 1";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, serviceId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                masters.add(extractMaster(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return masters;
    }

    // Получить портфолио мастера для конкретной услуги
    public List<String> getPortfolioImages(int masterId, int serviceId) {
        List<String> images = new ArrayList<>();
        String sql = "SELECT image_path FROM master_portfolio WHERE master_id = ? AND service_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            pstmt.setInt(2, serviceId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                images.add(rs.getString("image_path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }

    public List<String> getAllPositions() {
        List<String> positions = new ArrayList<>();
        String sql = "SELECT name FROM positions";
        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                positions.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }

    // Обновить данные мастера (без зарплаты)
    public boolean update(Master master) {
        String sql = "UPDATE masters SET name=?, phone=?, specialization=?, hire_date=?, is_active=? WHERE id=?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, master.getName());
            pstmt.setString(2, master.getPhone());
            pstmt.setString(3, master.getSpecialization());
            pstmt.setString(4, master.getHireDate());
            pstmt.setInt(5, master.isActive() ? 1 : 0);
            pstmt.setInt(6, master.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Обновить зарплату мастера (добавить новую запись в salaries)
    public boolean updateSalary(int masterId, double salary) {
        String sql = "INSERT INTO salaries (master_id, salary_amount, payment_date) VALUES (?, ?, DATE('now'))";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            pstmt.setDouble(2, salary);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Удалить мастера
    public boolean delete(int id) {
        // Сначала удаляем зарплаты (если есть)
        String deleteSalaries = "DELETE FROM salaries WHERE master_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deleteSalaries)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Удаляем портфолио (если есть)
        String deletePortfolio = "DELETE FROM master_portfolio WHERE master_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deletePortfolio)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Удаляем связи с услугами
        String deleteServices = "DELETE FROM master_services WHERE master_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deleteServices)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // Удаляем мастера
        String sql = "DELETE FROM masters WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public int createAndGetId(Master master) {
        String sql = "INSERT INTO masters (name, phone, specialization, hire_date, is_active, rating) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, master.getName());
            pstmt.setString(2, master.getPhone());
            pstmt.setString(3, master.getSpecialization());
            pstmt.setString(4, master.getHireDate());
            pstmt.setInt(5, master.isActive() ? 1 : 0);
            pstmt.setDouble(6, master.getRating());
            pstmt.executeUpdate();

            ResultSet rs = pstmt.getGeneratedKeys();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Вспомогательный метод для извлечения Master
    private Master extractMaster(ResultSet rs) throws SQLException {
        Master m = new Master();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("name"));
        m.setPhone(rs.getString("phone"));
        m.setSpecialization(rs.getString("specialization"));
        m.setDescription(rs.getString("description"));
        m.setRating(rs.getDouble("rating"));
        m.setHireDate(rs.getString("hire_date"));
        m.setPositionId(rs.getInt("position_id"));
        m.setActive(rs.getInt("is_active") == 1);
        m.setAvatarPath(rs.getString("photo_url"));
        return m;
    }
}