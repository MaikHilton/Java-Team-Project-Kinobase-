package com.kinobase.dao;

import com.kinobase.entity.Viewer;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-клас для роботи з таблицею {@code viewer}.
 *
 * @author Team Lead
 * @version 1.0
 */
public class ViewerDao {

    public void insert(Viewer viewer) throws SQLException {
        String sql = "INSERT INTO viewer (name) VALUES (?)";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, viewer.getName());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) viewer.setViewerId(keys.getInt(1));
            }
        }
    }

    public List<Viewer> findAll() throws SQLException {
        String sql = "SELECT * FROM viewer ORDER BY viewer_id";
        List<Viewer> list = new ArrayList<>();
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    public Viewer findById(int viewerId) throws SQLException {
        String sql = "SELECT * FROM viewer WHERE viewer_id = ?";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, viewerId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /** Пошук глядачів за частиною імені. */
    public List<Viewer> findByName(String namePart) throws SQLException {
        String sql = "SELECT * FROM viewer WHERE LOWER(name) LIKE LOWER(?)";
        List<Viewer> list = new ArrayList<>();
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, "%" + namePart + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void update(Viewer viewer) throws SQLException {
        String sql = "UPDATE viewer SET name = ? WHERE viewer_id = ?";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, viewer.getName());
            ps.setInt   (2, viewer.getViewerId());
            ps.executeUpdate();
        }
    }

    public void delete(int viewerId) throws SQLException {
        String sql = "DELETE FROM viewer WHERE viewer_id = ?";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, viewerId);
            ps.executeUpdate();
        }
    }

    private Viewer mapRow(ResultSet rs) throws SQLException {
        return new Viewer(rs.getInt("viewer_id"), rs.getString("name"));
    }
}
