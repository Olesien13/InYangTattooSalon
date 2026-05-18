package dao;

import models.Client;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClientDao {

    // Получить всех клиентов (даже без записей)
    public List<Client> getAll() {
        List<Client> clients = new ArrayList<>();
        String sql = """
            SELECT 
                u.id,
                u.last_name || ' ' || u.first_name || ' ' || u.middle_name as name,
                u.phone,
                u.email,
                u.created_at as registration_date,
                COALESCE(s.name, 'Нет записей') as service_name,
                COALESCE(a.appointment_date, '') as date,
                COALESCE(a.status, '') as status,
                COALESCE(a.final_price, s.price, 0) as total_spent,
                COALESCE(a.size, '') as size
            FROM users u
            LEFT JOIN appointments a ON u.id = a.user_id
            LEFT JOIN services s ON a.service_id = s.id
            WHERE u.role = 'client'
            ORDER BY u.id
        """;

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                clients.add(extractClient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // Получить клиента по id
    public Client findById(int id) {
        String sql = """
            SELECT 
                u.id,
                u.last_name || ' ' || u.first_name || ' ' || u.middle_name as name,
                u.phone,
                u.email,
                u.created_at as registration_date,
                COALESCE(s.name, 'Нет записей') as service_name,
                COALESCE(a.appointment_date, '') as date,
                COALESCE(a.status, '') as status,
                COALESCE(a.final_price, s.price, 0) as total_spent,
                COALESCE(a.size, '') as size
            FROM users u
            LEFT JOIN appointments a ON u.id = a.user_id
            LEFT JOIN services s ON a.service_id = s.id
            WHERE u.id = ? AND u.role = 'client'
        """;

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractClient(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Получить только клиентов с записями
    public List<Client> getWithAppointments() {
        List<Client> clients = new ArrayList<>();
        String sql = """
            SELECT 
                u.id,
                u.last_name || ' ' || u.first_name || ' ' || u.middle_name as name,
                u.phone,
                u.email,
                u.created_at as registration_date,
                s.name as service_name,
                a.appointment_date as date,
                a.status,
                COALESCE(a.final_price, s.price, 0) as total_spent,
                COALESCE(a.size, '') as size
            FROM users u
            JOIN appointments a ON u.id = a.user_id
            JOIN services s ON a.service_id = s.id
            WHERE u.role = 'client'
            ORDER BY a.appointment_date DESC
        """;

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                clients.add(extractClient(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return clients;
    }

    // Обновить данные клиента
    public boolean update(Client client) {
        String sql = "UPDATE users SET first_name=?, last_name=?, middle_name=?, phone=?, email=? WHERE id=?";

        String[] nameParts = client.getName().split(" ", 3);
        String lastName = nameParts[0];
        String firstName = nameParts.length > 1 ? nameParts[1] : "";
        String middleName = nameParts.length > 2 ? nameParts[2] : "";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, firstName);
            pstmt.setString(2, lastName);
            pstmt.setString(3, middleName);
            pstmt.setString(4, client.getPhone());
            pstmt.setString(5, client.getEmail());
            pstmt.setInt(6, client.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Обновить статус записи
    public boolean updateAppointmentStatus(int userId, String date, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE user_id = ? AND appointment_date = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, userId);
            pstmt.setString(3, date);

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Обновить цену записи
    public boolean updateAppointmentPrice(int userId, String date, double price) {
        String sql = "UPDATE appointments SET final_price = ? WHERE user_id = ? AND appointment_date = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setDouble(1, price);
            pstmt.setInt(2, userId);
            pstmt.setString(3, date);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Удалить клиента (и все его записи)
    public boolean delete(int id) {
        String deleteAppointments = "DELETE FROM appointments WHERE user_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(deleteAppointments)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        String sql = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private Client extractClient(ResultSet rs) throws SQLException {
        Client client = new Client();
        client.setId(rs.getInt("id"));
        client.setName(rs.getString("name"));
        client.setPhone(rs.getString("phone"));
        client.setEmail(rs.getString("email"));
        client.setRegistrationDate(rs.getString("registration_date"));
        client.setServiceName(rs.getString("service_name"));
        client.setDate(rs.getString("date"));
        client.setStatus(rs.getString("status"));
        client.setTotalSpent(rs.getDouble("total_spent"));
        client.setSize(rs.getString("size"));
        return client;
    }
}