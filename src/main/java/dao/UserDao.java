package dao;

import models.User;
import java.sql.*;

public class UserDao {

    // Найти пользователя по email
    public User findByEmail(String email) {
        String sql = "SELECT * FROM users WHERE email = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Найти пользователя по id
    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return extractUser(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Создать нового пользователя
    public boolean create(User user) {
        String sql = "INSERT INTO users (email, password_hash, first_name, last_name, middle_name, phone, role) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getEmail());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.setString(3, user.getFirstName());
            pstmt.setString(4, user.getLastName());
            pstmt.setString(5, user.getMiddleName());
            pstmt.setString(6, user.getPhone());
            pstmt.setString(7, user.getRole());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Обновить пользователя
    public boolean update(User user) {
        String sql = "UPDATE users SET first_name=?, last_name=?, middle_name=?, phone=?, password_hash=? WHERE id=?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getMiddleName());
            pstmt.setString(4, user.getPhone());
            pstmt.setString(5, user.getPasswordHash());
            pstmt.setInt(6, user.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Проверка логина
    public User login(String email, String password) {
        User user = findByEmail(email);
        if (user != null && user.getPasswordHash().equals(password)) {
            return user;
        }
        return null;
    }

    // Получить всех пользователей (для админа)
    public java.util.List<User> getAll() {
        java.util.List<User> users = new java.util.ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'client'";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                users.add(extractUser(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return users;
    }

    private User extractUser(ResultSet rs) throws SQLException {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setMiddleName(rs.getString("middle_name"));
        user.setPhone(rs.getString("phone"));
        user.setRole(rs.getString("role"));
        user.setCreatedAt(rs.getString("created_at"));
        return user;
    }

    //Обновить данные пользователя (имя, фамилия, телефон, email)
    public boolean updateUser(User user) {
        String sql = "UPDATE users SET first_name = ?, last_name = ?, phone = ?, email = ? WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, user.getFirstName());
            pstmt.setString(2, user.getLastName());
            pstmt.setString(3, user.getPhone());
            pstmt.setString(4, user.getEmail());
            pstmt.setInt(5, user.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updatePassword(int userId, String newPasswordHash) {
        String sql = "UPDATE users SET password_hash = ? WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, newPasswordHash);
            pstmt.setInt(2, userId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void incrementCompletedServicesAndUpdateDiscount(int userId) {
        // 1. Увеличиваем completed_services_count
        String incSql = "UPDATE users SET completed_services_count = completed_services_count + 1 WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(incSql)) {
            pstmt.setInt(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        // 2. Пересчитываем скидку
        String selectSql = "SELECT completed_services_count FROM users WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(selectSql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                int count = rs.getInt(1);
                int newDiscount = 0;
                if (count >= 10) newDiscount = 15;
                else if (count >= 5) newDiscount = 10;
                else if (count >= 1) newDiscount = 5;
                // Обновляем скидку
                String updateSql = "UPDATE users SET discount = ? WHERE id = ?";
                try (PreparedStatement pstmt2 = DatabaseConnection.getConnection().prepareStatement(updateSql)) {
                    pstmt2.setInt(1, newDiscount);
                    pstmt2.setInt(2, userId);
                    pstmt2.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}