package dao;

import models.Master;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MasterDao {

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

    /**
     * Получить мастеров, которые оказывают данную услугу.
     */
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

    /**
     * Получить портфолио мастера для конкретной услуги.
     */
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
        m.setAvatarPath(rs.getString("photo_url")); // фото аватарки
        return m;
    }
}