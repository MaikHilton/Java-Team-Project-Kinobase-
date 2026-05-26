package com.kinobase.dao;

import com.kinobase.entity.Genre;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для довідника жанрів {@code genre}.
 *
 * @author Team Lead
 * @version 1.0
 */
public class GenreDao {

    public void insert(Genre genre) throws SQLException {
        String sql = "INSERT INTO genre (genre_name) VALUES (?)";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, genre.getGenreName());
            ps.executeUpdate();
        }
    }

    public List<Genre> findAll() throws SQLException {
        String sql = "SELECT * FROM genre ORDER BY genre_name";
        List<Genre> list = new ArrayList<>();
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(new Genre(rs.getString("genre_name")));
        }
        return list;
    }

    public void delete(String genreName) throws SQLException {
        String sql = "DELETE FROM genre WHERE genre_name = ?";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, genreName);
            ps.executeUpdate();
        }
    }
}
