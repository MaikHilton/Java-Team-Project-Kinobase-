package com.kinobase.entity;

/**
 * Клас-сутність, що представляє глядача у системі Кінобаза.
 *
 * Відповідає таблиці {@code viewer} у базі даних.
 *
 * @author Developer 1
 * @version 1.0
 */
public class Viewer {

    /** Унікальний ідентифікатор глядача (PK, SERIAL). */
    private int viewerId;

    /** Ім'я глядача. */
    private String name;

    /** Конструктор без аргументів (для DAO). */
    public Viewer() {}

    /**
     * Конструктор для створення глядача.
     *
     * @param name ім'я глядача
     */
    public Viewer(String name) {
        this.name = name;
    }

    /**
     * Повний конструктор.
     *
     * @param viewerId id глядача
     * @param name     ім'я глядача
     */
    public Viewer(int viewerId, String name) {
        this.viewerId = viewerId;
        this.name     = name;
    }

    /** @return id глядача */
    public int getViewerId() { return viewerId; }

    /** @param viewerId id глядача */
    public void setViewerId(int viewerId) { this.viewerId = viewerId; }

    /** @return ім'я глядача */
    public String getName() { return name; }

    /** @param name ім'я глядача */
    public void setName(String name) { this.name = name; }

    @Override
    public String toString() {
        return String.format("Viewer{id=%d, name='%s'}", viewerId, name);
    }
}
