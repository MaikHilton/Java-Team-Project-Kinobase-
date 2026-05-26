package com.kinobase.dao;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

/**
 * Допоміжний клас для отримання JDBC-з'єднання з базою даних.
 *
 * Параметри підключення зчитуються з файлу
 * {@code src/main/resources/db.properties}.
 *
 * Патерн: Singleton – одне з'єднання на весь час роботи застосунку.
 *
 * @author Team Lead
 * @version 1.0
 */
public class JDBCHelper {

    private static Connection connection;

    /** Приватний конструктор – утилітний клас. */
    private JDBCHelper() {}

    /**
     * Повертає активне з'єднання з БД.
     * Якщо з'єднання відсутнє або закрите – створює нове.
     *
     * @return об'єкт {@link Connection}
     * @throws SQLException якщо підключення не вдалось
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = createConnection();
        }
        return connection;
    }

    /**
     * Створює нове підключення на основі {@code db.properties}.
     *
     * @return новий об'єкт {@link Connection}
     * @throws SQLException якщо параметри або підключення невалідні
     */
    private static Connection createConnection() throws SQLException {
        Properties props = loadProperties();
        String url      = props.getProperty("db.url");
        String user     = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        if (url == null || user == null || password == null) {
            throw new SQLException("Відсутні параметри підключення у db.properties");
        }
        return DriverManager.getConnection(url, user, password);
    }

    /**
     * Завантажує файл {@code db.properties} з classpath.
     *
     * @return об'єкт {@link Properties} з параметрами підключення
     * @throws SQLException якщо файл не знайдено або не вдалось прочитати
     */
    private static Properties loadProperties() throws SQLException {
        Properties props = new Properties();
        try (InputStream is = JDBCHelper.class
                .getClassLoader()
                .getResourceAsStream("db.properties")) {
            if (is == null) {
                throw new SQLException("Файл db.properties не знайдено у classpath");
            }
            props.load(is);
        } catch (IOException e) {
            throw new SQLException("Помилка читання db.properties: " + e.getMessage(), e);
        }
        return props;
    }

    /**
     * Закриває активне з'єднання з БД.
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                if (!connection.isClosed()) {
                    connection.close();
                }
            } catch (SQLException e) {
                System.err.println("Помилка при закритті з'єднання: " + e.getMessage());
            }
        }
    }
}
