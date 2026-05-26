package com.kinobase.dao;

import com.kinobase.entity.Country;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO для довідника країн {@code country}.
 *
 * @author Team Lead
 * @version 1.0
 */
public class CountryDao {

    public void insert(Country country) throws SQLException {
        String sql = "INSERT INTO country (country_name, exists) VALUES (?, ?)";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString (1, country.getCountryName());
            ps.setBoolean(2, country.isExists());
            ps.executeUpdate();
        }
    }

    public List<Country> findAll() throws SQLException {
        String sql = "SELECT * FROM country ORDER BY country_name";
        List<Country> list = new ArrayList<>();
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next())
                list.add(new Country(rs.getString("country_name"), rs.getBoolean("exists")));
        }
        return list;
    }

    public void update(Country country) throws SQLException {
        String sql = "UPDATE country SET exists = ? WHERE country_name = ?";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBoolean(1, country.isExists());
            ps.setString (2, country.getCountryName());
            ps.executeUpdate();
        }
    }

    public void delete(String countryName) throws SQLException {
        String sql = "DELETE FROM country WHERE country_name = ?";
        try (Connection c = JDBCHelper.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, countryName);
            ps.executeUpdate();
        }
    }
}
