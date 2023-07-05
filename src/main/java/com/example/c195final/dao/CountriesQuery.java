package com.example.c195final.dao;

import com.example.c195final.helper.JDBC;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CountriesQuery {

    public static int insert(String countryName, int countryId) throws SQLException {
        String sql = "INSERT INTO COUNTRIES (Country, Country_ID) VALUES(?, ?)";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,countryName);
        ps.setInt(2,countryId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    public static int update(int countryId, String countryName) throws SQLException {
        String sql = "UPDATE COUNTRIES SET Country = ? WHERE Country_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1,countryName);
        ps.setInt(2,countryId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    public static int delete(int countryId) throws SQLException {
        String sql = "DELETE FROM COUNTRIES WHERE Country_ID = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setInt(1,countryId);
        int rowsAffected = ps.executeUpdate();
        return rowsAffected;
    }

    public static void select() throws SQLException {
        String sql = "SELECT * FROM COUNTRIES";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int countryId = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");
            System.out.print(countryId + " | ");
            System.out.print(countryName + "\n");
        }
    }

    public static void select(String createdBy) throws SQLException {
        String sql = "SELECT * FROM COUNTRIES WHERE Created_By = ?";
        PreparedStatement ps = JDBC.connection.prepareStatement(sql);
        ps.setString(1, createdBy);
        ResultSet rs = ps.executeQuery();
        while(rs.next()){
            int countryId = rs.getInt("Country_ID");
            String countryName = rs.getString("Country");
            String createdByFK = rs.getString("Created_By");
            System.out.print(countryId + " | ");
            System.out.print(countryName + " | ");
            System.out.print(createdByFK + "\n");
        }
    }
}
