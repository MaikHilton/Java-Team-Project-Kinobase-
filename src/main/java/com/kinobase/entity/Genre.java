package com.kinobase.entity;

/**
 * Довідник жанрів фільмів.
 * Первинний ключ – рядковий ({@code genre_name}), що унеможливлює
 * присвоєння фільму неіснуючого жанру.
 *
 * @author Developer 1
 * @version 1.0
 */
public class Genre {

    /** Назва жанру (PK). */
    private String genreName;

    /**
     * Конструктор для створення жанру.
     *
     * @param genreName назва жанру
     */
    public Genre(String genreName) {
        this.genreName = genreName;
    }

    /** @return назва жанру */
    public String getGenreName() { return genreName; }

    /** @param genreName нова назва жанру */
    public void setGenreName(String genreName) { this.genreName = genreName; }

    @Override
    public String toString() { return genreName; }
}
