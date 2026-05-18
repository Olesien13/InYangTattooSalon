package dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReferenceImageDao {

    // Сохранить путь к референсу (эскизу) для конкретной записи.
    public boolean insert(int appointmentId, String imagePath, String fileName) {
        String sql = "INSERT INTO reference_images (appointment_id, image_path, file_name) VALUES (?, ?, ?)";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            pstmt.setString(2, imagePath);
            pstmt.setString(3, fileName);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Получить список референсов для записи.
    public List<String> getByAppointmentId(int appointmentId) {
        List<String> images = new ArrayList<>();
        String sql = "SELECT image_path FROM reference_images WHERE appointment_id = ?";
        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, appointmentId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                images.add(rs.getString("image_path"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return images;
    }
}