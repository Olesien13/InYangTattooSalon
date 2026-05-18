package dao;

import models.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppointmentDao {

    // Создать запись
    public boolean create(Appointment appointment) {
        String sql = "INSERT INTO appointments (user_id, master_id, service_id, appointment_date, appointment_time, status, size) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, appointment.getUserId());
            pstmt.setInt(2, appointment.getMasterId());
            pstmt.setInt(3, appointment.getServiceId());
            pstmt.setString(4, appointment.getAppointmentDate());
            pstmt.setString(5, appointment.getAppointmentTime());
            pstmt.setString(6, appointment.getStatus());
            pstmt.setString(7, appointment.getSize());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Проверить, свободен ли слот
    public boolean isSlotAvailable(int masterId, String date, String time) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE master_id = ? " +
                "AND appointment_date = ? AND appointment_time = ? " +
                "AND status IN ('pending', 'confirmed')";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            ResultSet rs = pstmt.executeQuery();
            return rs.getInt(1) == 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получить все записи пользователя
    public List<Appointment> findByUser(int userId) {
        List<Appointment> appointments = new ArrayList<>();
        String sql = "SELECT * FROM appointments WHERE user_id = ? ORDER BY appointment_date DESC";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                appointments.add(extractAppointment(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return appointments;
    }

    private Appointment extractAppointment(ResultSet rs) throws SQLException {
        Appointment appointment = new Appointment();
        appointment.setId(rs.getInt("id"));
        appointment.setUserId(rs.getInt("user_id"));
        appointment.setMasterId(rs.getInt("master_id"));
        appointment.setServiceId(rs.getInt("service_id"));
        appointment.setAppointmentDate(rs.getString("appointment_date"));
        appointment.setAppointmentTime(rs.getString("appointment_time"));
        appointment.setStatus(rs.getString("status"));
        appointment.setCreatedAt(rs.getString("created_at"));
        appointment.setSize(rs.getString("size"));
        return appointment;
    }
}