package com.kinobase.entity;

import java.math.BigDecimal;

/**
 * Клас-сутність, що представляє фільм у системі Кінобаза.
 *
 * Відповідає таблиці {@code movie} у базі даних.
 * Поле {@code avgRating} обчислюється через VIEW і заповнюється
 * лише при запитах зі статистикою.
 *
 * @author Developer 1
 * @version 1.0
 */
public class Movie {

    /** Унікальний ідентифікатор фільму (PK, SERIAL). */
    private int movieId;

    /** Назва фільму. */
    private String movieName;

    /** Жанр фільму (FK → genre.genre_name). */
    private String genre;

    /** Країна виробництва (FK → country.country_name). */
    private String country;

    /** Рік виходу фільму. */
    private int releaseYear;

    /**
     * Середній рейтинг фільму.
     *   Обчислюється лише якщо кількість голосів {@code > 10},
     * інакше дорівнює {@code 0}. 
     */
    private BigDecimal avgRating;

    /** Кількість голосів за фільм. */
    private int voteCount;

    /** Конструктор без аргументів (для DAO). */
    public Movie() {}

    /**
     * Основний конструктор для створення фільму.
     *
     * @param movieName   назва фільму
     * @param genre       жанр
     * @param country     країна
     * @param releaseYear рік виходу
     */
    public Movie(String movieName, String genre, String country, int releaseYear) {
        this.movieName   = movieName;
        this.genre       = genre;
        this.country     = country;
        this.releaseYear = releaseYear;
    }

    /** @return id фільму */
    public int getMovieId() { return movieId; }

    /** @param movieId id фільму */
    public void setMovieId(int movieId) { this.movieId = movieId; }

    /** @return назва фільму */
    public String getMovieName() { return movieName; }

    /** @param movieName назва фільму */
    public void setMovieName(String movieName) { this.movieName = movieName; }

    /** @return жанр фільму */
    public String getGenre() { return genre; }

    /** @param genre жанр фільму */
    public void setGenre(String genre) { this.genre = genre; }

    /** @return країна виробництва */
    public String getCountry() { return country; }

    /** @param country країна виробництва */
    public void setCountry(String country) { this.country = country; }

    /** @return рік виходу */
    public int getReleaseYear() { return releaseYear; }

    /** @param releaseYear рік виходу */
    public void setReleaseYear(int releaseYear) { this.releaseYear = releaseYear; }

    /** @return середній рейтинг (0 якщо {@code voteCount <= 10}) */
    public BigDecimal getAvgRating() { return avgRating; }

    /** @param avgRating середній рейтинг */
    public void setAvgRating(BigDecimal avgRating) { this.avgRating = avgRating; }

    /** @return кількість голосів */
    public int getVoteCount() { return voteCount; }

    /** @param voteCount кількість голосів */
    public void setVoteCount(int voteCount) { this.voteCount = voteCount; }

    @Override
    public String toString() {
        return String.format("Movie{id=%d, name='%s', genre='%s', country='%s', year=%d}",
                movieId, movieName, genre, country, releaseYear);
    }
}
