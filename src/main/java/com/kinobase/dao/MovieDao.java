package com.kinobase.dao;

import com.kinobase.entity.Movie;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO-клас для роботи з таблицею {@code movie} та VIEW {@code movie_rating_view}.
 *
 * Усі запити виконуються виключно через {@link PreparedStatement}
 * для захисту від SQL-ін'єкцій.
 *
 * @author Team Lead
 * @version 1.0
 */
public class MovieDao {

    // ─── CREATE ──────────────────────────────────────────────────────────────

    /**
     * Додає новий фільм до бази даних.
     *
     * @param movie об'єкт фільму (movieId ігнорується, генерується автоматично)
     * @throws SQLException при помилці вставки
     */
    public void insert(Movie movie) throws SQLException {
        String sql = "INSERT INTO movie (movie_name, genre, country, release_year) " +
                     "VALUES (?, ?, ?, ?)";
        try (Connection conn = JDBCHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, movie.getMovieName());
            ps.setString(2, movie.getGenre());
            ps.setString(3, movie.getCountry());
            ps.setInt   (4, movie.getReleaseYear());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) movie.setMovieId(keys.getInt(1));
            }
        }
    }

    // ─── READ ────────────────────────────────────────────────────────────────

    /**
     * Повертає всі фільми з рейтингом (через VIEW).
     *
     * @return список усіх фільмів
     * @throws SQLException при помилці запиту
     */
    public List<Movie> findAll() throws SQLException {
        String sql = "SELECT * FROM movie_rating_view ORDER BY movie_id";
        List<Movie> list = new ArrayList<>();
        try (Connection conn = JDBCHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(mapRow(rs));
        }
        return list;
    }

    /**
     * Знаходить фільм за ідентифікатором.
     *
     * @param movieId ідентифікатор фільму
     * @return фільм або {@code null}
     * @throws SQLException при помилці запиту
     */
    public Movie findById(int movieId) throws SQLException {
        String sql = "SELECT * FROM movie_rating_view WHERE movie_id = ?";
        try (Connection conn = JDBCHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }
        }
    }

    /**
     * Пошук фільмів за жанром.
     *
     * @param genre назва жанру
     * @return список фільмів
     * @throws SQLException при помилці запиту
     */
    public List<Movie> findByGenre(String genre) throws SQLException {
        String sql = "SELECT * FROM movie_rating_view WHERE genre = ? ORDER BY movie_id";
        return queryList(sql, genre);
    }

    /**
     * Пошук фільмів за країною.
     *
     * @param country назва країни
     * @return список фільмів
     * @throws SQLException при помилці запиту
     */
    public List<Movie> findByCountry(String country) throws SQLException {
        String sql = "SELECT * FROM movie_rating_view WHERE country = ? ORDER BY movie_id";
        return queryList(sql, country);
    }

    /**
     * Пошук фільмів за діапазоном років.
     *
     * @param yearFrom рік від (включно)
     * @param yearTo   рік до (включно)
     * @return список фільмів
     * @throws SQLException при помилці запиту
     */
    public List<Movie> findByYearRange(int yearFrom, int yearTo) throws SQLException {
        String sql = "SELECT * FROM movie_rating_view " +
                     "WHERE release_year BETWEEN ? AND ? ORDER BY release_year";
        List<Movie> list = new ArrayList<>();
        try (Connection conn = JDBCHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, yearFrom);
            ps.setInt(2, yearTo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Пошук фільмів за жанром ТА країною (2 критерії одночасно).
     *
     * @param genre   жанр
     * @param country країна
     * @return список фільмів
     * @throws SQLException при помилці запиту
     */
    public List<Movie> findByGenreAndCountry(String genre, String country) throws SQLException {
        String sql = "SELECT * FROM movie_rating_view WHERE genre = ? AND country = ?";
        List<Movie> list = new ArrayList<>();
        try (Connection conn = JDBCHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, genre);
            ps.setString(2, country);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Пошук за частиною назви фільму (ILIKE – регістронезалежний).
     *
     * @param namePart частина назви
     * @return список фільмів
     * @throws SQLException при помилці запиту
     */
    public List<Movie> findByName(String namePart) throws SQLException {
        String sql = "SELECT * FROM movie_rating_view WHERE LOWER(movie_name) LIKE LOWER(?)";
        List<Movie> list = new ArrayList<>();
        try (Connection conn = JDBCHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, "%" + namePart + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    // ─── UPDATE ──────────────────────────────────────────────────────────────

    /**
     * Оновлює дані фільму.
     *
     * @param movie об'єкт фільму з оновленими полями
     * @throws SQLException при помилці оновлення
     */
    public void update(Movie movie) throws SQLException {
        String sql = "UPDATE movie SET movie_name=?, genre=?, country=?, release_year=? " +
                     "WHERE movie_id=?";
        try (Connection conn = JDBCHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, movie.getMovieName());
            ps.setString(2, movie.getGenre());
            ps.setString(3, movie.getCountry());
            ps.setInt   (4, movie.getReleaseYear());
            ps.setInt   (5, movie.getMovieId());
            ps.executeUpdate();
        }
    }

    // ─── DELETE ──────────────────────────────────────────────────────────────

    /**
     * Видаляє фільм за ідентифікатором.
     *
     * @param movieId ідентифікатор фільму
     * @throws SQLException при помилці видалення
     */
    public void delete(int movieId) throws SQLException {
        String sql = "DELETE FROM movie WHERE movie_id = ?";
        try (Connection conn = JDBCHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, movieId);
            ps.executeUpdate();
        }
    }

    // ─── PRIVATE ─────────────────────────────────────────────────────────────

    private List<Movie> queryList(String sql, String param) throws SQLException {
        List<Movie> list = new ArrayList<>();
        try (Connection conn = JDBCHelper.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, param);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapRow(rs));
            }
        }
        return list;
    }

    /**
     * Маппінг рядка ResultSet у об'єкт {@link Movie}.
     *
     * @param rs ResultSet, вказівник стоїть на поточному рядку
     * @return заповнений об'єкт Movie
     * @throws SQLException при помилці читання
     */
    private Movie mapRow(ResultSet rs) throws SQLException {
        Movie m = new Movie();
        m.setMovieId    (rs.getInt       ("movie_id"));
        m.setMovieName  (rs.getString    ("movie_name"));
        m.setGenre      (rs.getString    ("genre"));
        m.setCountry    (rs.getString    ("country"));
        m.setReleaseYear(rs.getInt       ("release_year"));
        // Ці колонки є лише у movie_rating_view
        try {
            m.setVoteCount (rs.getInt       ("vote_count"));
            BigDecimal avg = rs.getBigDecimal("avg_rating");
            m.setAvgRating (avg != null ? avg : BigDecimal.ZERO);
        } catch (SQLException ignored) { /* звичайна таблиця без view */ }
        return m;
    }
}
