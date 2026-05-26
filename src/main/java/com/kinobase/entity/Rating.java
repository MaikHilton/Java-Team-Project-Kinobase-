package com.kinobase.entity;

/**
 * Клас-сутність, що представляє оцінку фільму глядачем.
 *
 * Відповідає таблиці {@code rating} у базі даних.
 * Складений первинний ключ: {@code (viewer_id, movie_id)}. 
 *
 *   Бізнес-правило: Рейтинг фільму обчислюється лише якщо
 * проголосувало {@code > 10} глядачів, інакше рейтинг = 0. 
 *
 * @author Developer 1
 * @version 1.0
 */
public class Rating {

    /** Id глядача (частина PK, FK → viewer). */
    private int viewerId;

    /** Id фільму (частина PK, FK → movie). */
    private int movieId;

    /** Оцінка від 1 до 10. */
    private int rating;

    /** Ім'я глядача (для відображення). */
    private String viewerName;

    /** Назва фільму (для відображення). */
    private String movieName;

    /** Конструктор без аргументів. */
    public Rating() {}

    /**
     * Основний конструктор.
     *
     * @param viewerId id глядача
     * @param movieId  id фільму
     * @param rating   оцінка (1–10)
     */
    public Rating(int viewerId, int movieId, int rating) {
        this.viewerId = viewerId;
        this.movieId  = movieId;
        this.rating   = rating;
    }

    /** @return id глядача */
    public int getViewerId() { return viewerId; }

    /** @param viewerId id глядача */
    public void setViewerId(int viewerId) { this.viewerId = viewerId; }

    /** @return id фільму */
    public int getMovieId() { return movieId; }

    /** @param movieId id фільму */
    public void setMovieId(int movieId) { this.movieId = movieId; }

    /** @return оцінка */
    public int getRating() { return rating; }

    /** @param rating оцінка (1–10) */
    public void setRating(int rating) { this.rating = rating; }

    /** @return ім'я глядача */
    public String getViewerName() { return viewerName; }

    /** @param viewerName ім'я глядача */
    public void setViewerName(String viewerName) { this.viewerName = viewerName; }

    /** @return назва фільму */
    public String getMovieName() { return movieName; }

    /** @param movieName назва фільму */
    public void setMovieName(String movieName) { this.movieName = movieName; }

    @Override
    public String toString() {
        return String.format("Rating{viewerId=%d, movieId=%d, rating=%d}",
                viewerId, movieId, rating);
    }
}
