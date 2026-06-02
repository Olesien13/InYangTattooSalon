package dao;

import models.Master;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// dao для работы с мастерами

public class MasterDao {

    // список всех активных мастеров
    public List<Master> getAll() {
        List<Master> masters = new ArrayList<>();
        String sql = "SELECT id, name, phone, specialization, description, rating, hire_date, is_active, photo_url FROM masters WHERE is_active = 1";
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

    // поиск мастера по id
    public Master findById(int id) {
        String sql = "SELECT id, name, phone, specialization, description, rating, hire_date, is_active, photo_url FROM masters WHERE id = ?";
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

    // мастера, которые могут выполнить услугу
    public List<Master> getByServiceId(int serviceId) {
        List<Master> masters = new ArrayList<>();
        String sql = "SELECT m.id, m.name, m.phone, m.specialization, m.description, m.rating, m.hire_date, m.is_active, m.photo_url " +
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

    // портфолио мастера для услуги
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

    // все уникальные специализации (для выпадающего списка должностей)
    public List<String> getAllPositions() {
        List<String> positions = new ArrayList<>();
        String sql = "SELECT DISTINCT specialization FROM masters WHERE specialization IS NOT NULL AND specialization != ''";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                positions.add(rs.getString("specialization"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }

    // обновление данных мастера (без зарплаты)
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

    // добавление записи о зарплате
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

    // сохранение зарплаты (дублирует updateSalary)
    public boolean saveSalary(int masterId, double salary) {
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

    // удаление мастера и связанных записей
    public boolean delete(int id) {
        String deleteSalaries = "DELETE FROM salaries WHERE master_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deleteSalaries)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String deletePortfolio = "DELETE FROM master_portfolio WHERE master_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deletePortfolio)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String deleteServices = "DELETE FROM master_services WHERE master_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deleteServices)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        String sql = "DELETE FROM masters WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // создание мастера с возвратом id
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
                int id = rs.getInt(1);
                saveSalary(id, master.getSalary());
                return id;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // получение последней зарплаты мастера
    private double getLatestSalary(int masterId) {
        String sql = "SELECT salary_amount FROM salaries WHERE master_id = ? ORDER BY payment_date DESC LIMIT 1";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("salary_amount");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    // извлечение мастера из resultset
    private Master extractMaster(ResultSet rs) throws SQLException {
        Master m = new Master();
        m.setId(rs.getInt("id"));
        m.setName(rs.getString("name"));
        m.setPhone(rs.getString("phone"));
        m.setSpecialization(rs.getString("specialization"));
        m.setDescription(rs.getString("description"));
        m.setRating(rs.getDouble("rating"));
        m.setHireDate(rs.getString("hire_date"));
        m.setActive(rs.getInt("is_active") == 1);
        m.setAvatarPath(rs.getString("photo_url"));
        m.setSalary(getLatestSalary(m.getId()));
        return m;
    }
}