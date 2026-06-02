package dao;

import models.Appointment;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// dao для работы с записями клиентов

public class AppointmentDao {

    // проверка, свободен ли слот у мастера
    // учитываются записи со статусами pending и confirmed
    public boolean isSlotAvailable(int masterId, String date, String time) {
        String sql = "SELECT COUNT(*) FROM appointments WHERE master_id = ? AND appointment_date = ? AND appointment_time = ? AND status IN ('pending', 'confirmed')";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            pstmt.setString(2, date);
            pstmt.setString(3, time);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0;   // если 0 – слот свободен
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true;   // по умолчанию считаем свободным
    }

    // создание новой записи (с учётом размера тату) – возвращает true/false
    public boolean createAppointment(Appointment appointment) {
        String sql = "INSERT INTO appointments (user_id, master_id, service_id, appointment_date, appointment_time, status, final_price, size) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, appointment.getUserId());
            pstmt.setInt(2, appointment.getMasterId());
            pstmt.setInt(3, appointment.getServiceId());
            pstmt.setString(4, appointment.getDate());
            pstmt.setString(5, appointment.getTime());
            pstmt.setString(6, appointment.getStatus());
            pstmt.setDouble(7, appointment.getFinalPrice());
            pstmt.setString(8, appointment.getSize());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // создание записи и возврат сгенерированного ID (нужен для сохранения референсов)
    public int createAndGetId(Appointment appointment) {
        String sql = "INSERT INTO appointments (user_id, master_id, service_id, appointment_date, appointment_time, status, final_price, size) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, appointment.getUserId());
            pstmt.setInt(2, appointment.getMasterId());
            pstmt.setInt(3, appointment.getServiceId());
            pstmt.setString(4, appointment.getDate());
            pstmt.setString(5, appointment.getTime());
            pstmt.setString(6, appointment.getStatus());
            pstmt.setDouble(7, appointment.getFinalPrice());
            pstmt.setString(8, appointment.getSize());
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

    // список всех записей пользователя (с данными о мастере и услуге)
    public List<Appointment> getByUserId(int userId) {
        List<Appointment> list = new ArrayList<>();
        String sql = "SELECT a.*, m.name as master_name, s.name as service_name, s.price as original_price " +
                "FROM appointments a " +
                "JOIN masters m ON a.master_id = m.id " +
                "JOIN services s ON a.service_id = s.id " +
                "WHERE a.user_id = ? ORDER BY a.appointment_date DESC, a.appointment_time DESC";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                Appointment a = new Appointment();
                a.setId(rs.getInt("id"));
                a.setUserId(rs.getInt("user_id"));
                a.setMasterId(rs.getInt("master_id"));
                a.setMasterName(rs.getString("master_name"));
                a.setServiceId(rs.getInt("service_id"));
                a.setServiceName(rs.getString("service_name"));
                a.setDate(rs.getString("appointment_date"));
                a.setTime(rs.getString("appointment_time"));
                a.setStatus(rs.getString("status"));
                a.setOriginalPrice(rs.getDouble("original_price"));
                a.setFinalPrice(rs.getDouble("final_price"));
                a.setSize(rs.getString("size"));
                list.add(a);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // обновление статуса записи (для отмены, подтверждения, завершения)
    public boolean updateStatus(int appointmentId, String status) {
        String sql = "UPDATE appointments SET status = ? WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, status);
            pstmt.setInt(2, appointmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // перенос записи на новую дату и время
    public boolean reschedule(int appointmentId, String newDate, String newTime) {
        String sql = "UPDATE appointments SET appointment_date = ?, appointment_time = ? WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, newDate);
            pstmt.setString(2, newTime);
            pstmt.setInt(3, appointmentId);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}