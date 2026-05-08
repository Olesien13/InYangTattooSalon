package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MasterScheduleDao {

    // Получить расписание мастера на день недели
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

    // Получить все рабочие дни мастера
    public List<Integer> getWorkingDays(int masterId) {
        List<Integer> days = new ArrayList<>();
        String sql = "SELECT day_of_week FROM master_schedule WHERE master_id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, masterId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                days.add(rs.getInt("day_of_week"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return days;
    }
}