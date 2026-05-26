package com.kinobase.dao;

import com.kinobase.entity.Rating;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-клас для роботи з таблицею {@code rating}.
 *
 * Складений PK: {@code (viewer_id, movie_id)}.
 *
 * @author Team Lead
 * @version 1.0
 */
public class RatingDao {

    public void insert(Rating rating) throws SQLException {
        String sql = "INSERT INTO rating (viewer_id, movie_id, rating) VALUES (?, ?, ?)";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, rating.getViewerId());
            ps.setInt(2, rating.getMovieId());
            ps.setInt(3, rating.getRating());
            ps.executeUpdate();
        }
    }

    /** Повертає всі оцінки з іменами глядачів і назвами фільмів. */
    public List<Rating> findAll() throws SQLException {
        String sql = """
                SELECT r.viewer_id, r.movie_id, r.rating,
                       v.name AS viewer_name, m.movie_name
                FROM rating r
                JOIN viewer v ON v.viewer_id = r.viewer_id
                JOIN movie  m ON m.movie_id  = r.movie_id
                ORDER BY r.movie_id, r.viewer_id
                """;
        List<Rating> list = new ArrayList<>();
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /** Усі оцінки конкретного глядача (для вкладки «Мої оцінки»). */
    public List<Rating> findByViewer(int viewerId) throws SQLException {
        String sql = """
                SELECT r.viewer_id, r.movie_id, r.rating,
                       v.name AS viewer_name, m.movie_name
                FROM rating r
                JOIN viewer v ON v.viewer_id = r.viewer_id
                JOIN movie  m ON m.movie_id  = r.movie_id
                WHERE r.viewer_id = ?
                ORDER BY r.movie_id
                """;
        List<Rating> list = new ArrayList<>();
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, viewerId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /** Усі оцінки конкретного фільму. */
    public List<Rating> findByMovie(int movieId) throws SQLException {
        String sql = """
                SELECT r.viewer_id, r.movie_id, r.rating,
                       v.name AS viewer_name, m.movie_name
                FROM rating r
                JOIN viewer v ON v.viewer_id = r.viewer_id
                JOIN movie  m ON m.movie_id  = r.movie_id
                WHERE r.movie_id = ?
                ORDER BY r.viewer_id
                """;
        List<Rating> list = new ArrayList<>();
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    public void update(Rating rating) throws SQLException {
        String sql = "UPDATE rating SET rating = ? WHERE viewer_id = ? AND movie_id = ?";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, rating.getRating());
            ps.setInt(2, rating.getViewerId());
            ps.setInt(3, rating.getMovieId());
            ps.executeUpdate();
        }
    }

    public void delete(int viewerId, int movieId) throws SQLException {
        String sql = "DELETE FROM rating WHERE viewer_id = ? AND movie_id = ?";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, viewerId);
            ps.setInt(2, movieId);
            ps.executeUpdate();
        }
    }

    private Rating mapRow(ResultSet rs) throws SQLException {
        Rating r = new Rating(rs.getInt("viewer_id"), rs.getInt("movie_id"), rs.getInt("rating"));
        r.setViewerName(rs.getString("viewer_name"));
        r.setMovieName (rs.getString("movie_name"));
        return r;
    }
}
