package com.kinobase.service;

import com.kinobase.dao.MovieDao;
import com.kinobase.dao.RatingDao;
import com.kinobase.entity.Movie;
import com.kinobase.entity.Rating;

import java.sql.SQLException;
import java.util.List;

/**
 * Сервісний клас з бізнес-логікою для фільмів та рейтингів.
 *
 *   Бізнес-правило:   рейтинг фільму відображається лише якщо
 * кількість голосів {@code > 10}, інакше = 0 (реалізовано у VIEW на рівні БД
 * та продубльовано тут для валідації на рівні Java). 
 *
 * @author Developer 1
 * @version 1.0
 */
public class MovieService {

    private final MovieDao  movieDao  = new MovieDao();
    private final RatingDao ratingDao = new RatingDao();

    /** Мінімальна кількість голосів для відображення рейтингу. */
    private static final int MIN_VOTES_FOR_RATING = 10;

    // ─── Фільми ──────────────────────────────────────────────────────────────

    public List<Movie> getAllMovies() throws SQLException {
        return movieDao.findAll();
    }

    public List<Movie> searchByGenre(String genre) throws SQLException {
        return movieDao.findByGenre(genre);
    }

    public List<Movie> searchByCountry(String country) throws SQLException {
        return movieDao.findByCountry(country);
    }

    public List<Movie> searchByYearRange(int from, int to) throws SQLException {
        if (from > to) throw new IllegalArgumentException("Рік 'від' не може бути більшим за 'до'");
        return movieDao.findByYearRange(from, to);
    }

    public List<Movie> searchByGenreAndCountry(String genre, String country) throws SQLException {
        return movieDao.findByGenreAndCountry(genre, country);
    }

    public List<Movie> searchByName(String name) throws SQLException {
        return movieDao.findByName(name);
    }

    public void addMovie(Movie movie) throws SQLException {
        validateMovie(movie);
        movieDao.insert(movie);
    }

    public void updateMovie(Movie movie) throws SQLException {
        validateMovie(movie);
        movieDao.update(movie);
    }

    public void deleteMovie(int movieId) throws SQLException {
        movieDao.delete(movieId);
    }

    // ─── Оцінки ──────────────────────────────────────────────────────────────

    /**
     * Додає або оновлює оцінку глядача.
     *
     * @param viewerId id глядача
     * @param movieId  id фільму
     * @param score    оцінка 1–10
     * @throws SQLException при помилці БД
     */
    public void rateMovie(int viewerId, int movieId, int score) throws SQLException {
        if (score < 1 || score > 10)
            throw new IllegalArgumentException("Оцінка має бути від 1 до 10");
        Rating r = new Rating(viewerId, movieId, score);
        try {
            ratingDao.insert(r);
        } catch (SQLException e) {
            // якщо запис вже існує – оновлюємо
            if (e.getSQLState() != null && e.getSQLState().startsWith("23")) {
                ratingDao.update(r);
            } else {
                throw e;
            }
        }
    }

    /** Повертає список оцінок конкретного глядача. */
    public List<Rating> getViewerRatings(int viewerId) throws SQLException {
        return ratingDao.findByViewer(viewerId);
    }

    /**
     * Перевіряє, чи пройшов фільм поріг голосування для відображення рейтингу.
     *
     * @param movie фільм з заповненим {@code voteCount}
     * @return {@code true} якщо рейтинг слід показувати
     */
    public boolean isRatingVisible(Movie movie) {
        return movie.getVoteCount() > MIN_VOTES_FOR_RATING;
    }

    // ─── Валідація ───────────────────────────────────────────────────────────

    private void validateMovie(Movie movie) {
        if (movie.getMovieName() == null || movie.getMovieName().isBlank())
            throw new IllegalArgumentException("Назва фільму не може бути порожньою");
        if (movie.getReleaseYear() < 1888 || movie.getReleaseYear() > 2100)
            throw new IllegalArgumentException("Рік виходу некоректний (1888–2100)");
    }
}
