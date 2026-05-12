package dao;

import models.Master;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MasterDao {

    // Получить всех мастеров
    public List<Master> getAll() {
        List<Master> masters = new ArrayList<>();
        String sql = "SELECT * FROM masters WHERE is_active = 1";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
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
        String sql = "SELECT * FROM masters WHERE id = ?";

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

    // Получить портфолио мастера
    public List<String> getPortfolioImages(int masterId) {
        List<String> images = new ArrayList<>();
        String sql = "SELECT image_path FROM master_portfolio WHERE master_id = ?";

        try (PreparedStatement stmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            stmt.setInt(1, masterId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                images.add(rs.getString("image_path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }

    // Создать мастера и вернуть его ID
    public int createAndGetId(Master master) {
        String sql = "INSERT INTO masters (name, phone, specialization, hire_date, is_active, rating) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

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

    // Сохранить зарплату мастера
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

    // Удалить мастера
    public boolean delete(int id) {
        String sql = "DELETE FROM masters WHERE id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получить последнюю зарплату мастера
    private double getSalary(int masterId) {
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

    // Обновить данные мастера
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

    // Обновить зарплату мастера
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

    // Получить все должности из таблицы positions
    public List<String> getAllPositions() {
        List<String> positions = new ArrayList<>();
        String sql = "SELECT name FROM positions";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                positions.add(rs.getString("name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return positions;
    }


    private Master extractMaster(ResultSet rs) throws SQLException {
        Master master = new Master();
        master.setId(rs.getInt("id"));
        master.setName(rs.getString("name"));
        master.setPhone(rs.getString("phone"));
        master.setSpecialization(rs.getString("specialization"));
        master.setDescription(rs.getString("description"));
        master.setRating(rs.getDouble("rating"));
        master.setHireDate(rs.getString("hire_date"));
        master.setPositionId(rs.getInt("position_id"));
        master.setActive(rs.getInt("is_active") == 1);

        // Подтягиваем зарплату
        master.setSalary(getSalary(master.getId()));

        return master;
    }
}
