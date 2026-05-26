-- =====================================================
-- Кінобаза: Скрипт створення таблиць (01_create_tables.sql)
-- Автор: Team Lead
-- =====================================================

-- Очищення (якщо перестворюємо)
DROP TABLE IF EXISTS rating   CASCADE;
DROP TABLE IF EXISTS movie    CASCADE;
DROP TABLE IF EXISTS viewer   CASCADE;
DROP TABLE IF EXISTS genre    CASCADE;
DROP TABLE IF EXISTS country  CASCADE;

-- ─────────────────────────────────────────
-- Довідник жанрів (рядковий PK)
-- ─────────────────────────────────────────
CREATE TABLE genre (
    genre_name VARCHAR(100) PRIMARY KEY
);

-- ─────────────────────────────────────────
-- Довідник країн (рядковий PK + exists)
-- ─────────────────────────────────────────
CREATE TABLE country (
    country_name VARCHAR(100) PRIMARY KEY,
    "exists"    BOOLEAN NOT NULL DEFAULT TRUE
);

-- ─────────────────────────────────────────
-- Каталог фільмів
-- ─────────────────────────────────────────
CREATE TABLE movie (
    movie_id     SERIAL       PRIMARY KEY,
    movie_name   VARCHAR(100) NOT NULL,
    genre        VARCHAR(100) REFERENCES genre(genre_name)   ON UPDATE CASCADE ON DELETE SET NULL,
    country      VARCHAR(100) REFERENCES country(country_name) ON UPDATE CASCADE ON DELETE SET NULL,
    release_year INT          NOT NULL CHECK (release_year >= 1888 AND release_year <= 2100)
);

-- ─────────────────────────────────────────
-- Глядачі системи
-- ─────────────────────────────────────────
CREATE TABLE viewer (
    viewer_id SERIAL       PRIMARY KEY,
    name      VARCHAR(100) NOT NULL
);

-- ─────────────────────────────────────────
-- Оцінки (M:N між movie і viewer)
-- ─────────────────────────────────────────
CREATE TABLE rating (
    viewer_id INT REFERENCES viewer(viewer_id) ON DELETE CASCADE,
    movie_id  INT REFERENCES movie(movie_id)   ON DELETE CASCADE,
    rating    INT NOT NULL CHECK (rating >= 1 AND rating <= 10),
    PRIMARY KEY (viewer_id, movie_id)
);

-- ─────────────────────────────────────────
-- View: рейтинг фільму (бізнес-правило: > 10 голосів)
-- ─────────────────────────────────────────
CREATE OR REPLACE VIEW movie_rating_view AS
SELECT
    m.movie_id,
    m.movie_name,
    m.genre,
    m.country,
    m.release_year,
    COUNT(r.viewer_id)            AS vote_count,
    CASE
        WHEN COUNT(r.viewer_id) > 10
        THEN ROUND(AVG(r.rating)::NUMERIC, 2)
        ELSE 0
    END                           AS avg_rating
FROM movie m
LEFT JOIN rating r ON m.movie_id = r.movie_id
GROUP BY m.movie_id, m.movie_name, m.genre, m.country, m.release_year;
