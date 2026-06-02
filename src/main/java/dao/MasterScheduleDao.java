package dao;

import models.MasterSchedule;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// dao для работы с расписанием мастеров

public class MasterScheduleDao {

    // получить рабочие часы мастера в определённый день недели
    public String[] getWorkingHours(int masterId, int dayOfWeek) {
        String sql = "SELECT start_time, end_time FROM master_schedule WHERE master_id = ? AND day_of_week = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            pstmt.setInt(2, dayOfWeek);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return new String[]{rs.getString("start_time"), rs.getString("end_time")};
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // получить всё расписание мастера по id
    public List<MasterSchedule> getByMasterId(int masterId) {
        List<MasterSchedule> schedules = new ArrayList<>();
        String sql = "SELECT id, master_id, day_of_week, start_time, end_time FROM master_schedule WHERE master_id = ? ORDER BY day_of_week";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                MasterSchedule schedule = new MasterSchedule();
                schedule.setId(rs.getInt("id"));
                schedule.setMasterId(rs.getInt("master_id"));
                schedule.setDayOfWeek(rs.getInt("day_of_week"));
                schedule.setStartTime(rs.getString("start_time"));
                schedule.setEndTime(rs.getString("end_time"));
                schedules.add(schedule);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return schedules;
    }

    // удалить всё расписание мастера
    public boolean deleteByMasterId(int masterId) {
        String sql = "DELETE FROM master_schedule WHERE master_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            int affected = pstmt.executeUpdate();
            return affected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // добавить рабочий день
    public boolean create(MasterSchedule schedule) {
        String sql = "INSERT INTO master_schedule (master_id, day_of_week, start_time, end_time) VALUES (?, ?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, schedule.getMasterId());
            pstmt.setInt(2, schedule.getDayOfWeek());
            pstmt.setString(3, schedule.getStartTime());
            pstmt.setString(4, schedule.getEndTime());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // обновить рабочий день
    public boolean update(MasterSchedule schedule) {
        String sql = "UPDATE master_schedule SET start_time = ?, end_time = ? WHERE id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, schedule.getStartTime());
            pstmt.setString(2, schedule.getEndTime());
            pstmt.setInt(3, schedule.getId());
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}