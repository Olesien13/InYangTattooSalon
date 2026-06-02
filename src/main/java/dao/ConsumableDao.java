package dao;

import models.Consumable;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// dao для работы с расходными материалами

public class ConsumableDao {

    // получить все расходники
    public List<Consumable> getAll() {
        List<Consumable> consumables = new ArrayList<>();
        String sql = "SELECT * FROM consumables";

        try (Statement stmt = DatabaseConnection.getConnection().createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                consumables.add(extractConsumable(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return consumables;
    }

    // найти расходник по id
    public Consumable findById(int id) {
        String sql = "SELECT * FROM consumables WHERE id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractConsumable(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // создать расходник
    public boolean create(Consumable consumable) {
        String sql = "INSERT INTO consumables (name, quantity, price, unit) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, consumable.getName());
            pstmt.setInt(2, consumable.getQuantity());
            pstmt.setDouble(3, consumable.getPrice());
            pstmt.setString(4, consumable.getUnit());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // создать и вернуть id
    public int createAndGetId(Consumable consumable) {
        String sql = "INSERT INTO consumables (name, quantity, price, unit) VALUES (?, ?, ?, ?)";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, consumable.getName());
            pstmt.setInt(2, consumable.getQuantity());
            pstmt.setDouble(3, consumable.getPrice());
            pstmt.setString(4, consumable.getUnit());

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

    // обновить расходник
    public boolean update(Consumable consumable) {
        String sql = "UPDATE consumables SET name=?, quantity=?, price=?, unit=? WHERE id=?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setString(1, consumable.getName());
            pstmt.setInt(2, consumable.getQuantity());
            pstmt.setDouble(3, consumable.getPrice());
            pstmt.setString(4, consumable.getUnit());
            pstmt.setInt(5, consumable.getId());

            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // удалить расходник
    public boolean delete(int id) {
        String sql = "DELETE FROM consumables WHERE id = ?";

        try (PreparedStatement pstmt = DatabaseConnection.getConnection().prepareStatement(sql)) {
            pstmt.setInt(1, id);
            return pstmt.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // извлечение расходника из resultset
    private Consumable extractConsumable(ResultSet rs) throws SQLException {
        Consumable consumable = new Consumable();
        consumable.setId(rs.getInt("id"));
        consumable.setName(rs.getString("name"));
        consumable.setQuantity(rs.getInt("quantity"));
        consumable.setPrice(rs.getDouble("price"));
        consumable.setUnit(rs.getString("unit"));
        return consumable;
    }
}