package com.kinobase.entity;

/**
 * Довідник країн виробництва фільмів.
 * Первинний ключ – рядковий ({@code country_name}).
 * Поле {@code exists} вказує, чи існує країна зараз
 * (наприклад, СРСР – {@code false}).
 *
 * @author Developer 1
 * @version 1.0
 */
public class Country {

    /** Назва країни (PK). */
    private String countryName;

    /** Чи існує країна зараз. */
    private boolean exists;

    /**
     * Конструктор для створення країни.
     *
     * @param countryName назва країни
     * @param exists      {@code true} якщо країна існує
     */
    public Country(String countryName, boolean exists) {
        this.countryName = countryName;
        this.exists = exists;
    }

    /** @return назва країни */
    public String getCountryName() { return countryName; }

    /** @param countryName нова назва країни */
    public void setCountryName(String countryName) { this.countryName = countryName; }

    /** @return {@code true} якщо країна існує */
    public boolean isExists() { return exists; }

    /** @param exists статус існування країни */
    public void setExists(boolean exists) { this.exists = exists; }

    @Override
    public String toString() { return countryName + (exists ? "" : " (не існує)"); }
}
