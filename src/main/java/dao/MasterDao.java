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
        return master;
    }
}