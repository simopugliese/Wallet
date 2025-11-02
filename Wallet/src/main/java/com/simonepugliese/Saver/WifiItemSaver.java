package com.simonepugliese.Saver;

import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.WifiItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WifiItemSaver extends Saver{
    private static final String TABLE_NAME = "Wifi";
    private static final String UPDATE_SQL =
            "UPDATE " + TABLE_NAME + " SET SSID = ?, password = ? WHERE description = ?";
    private static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (ssid, password, description) VALUES (?, ?, ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    @Override
    public void salva(Item item) {
        WifiItem wifiItem = (WifiItem) item;

        try (Connection conn = DbConnector.getInstance().getConnection()) {
            PreparedStatement updateStmt = conn.prepareStatement(UPDATE_SQL);
            updateStmt.setString(1, wifiItem.getSSID());
            updateStmt.setString(2, wifiItem.getPassword());
            updateStmt.setString(3, wifiItem.getDescription());

            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows == 0) {
                PreparedStatement insertStmt = conn.prepareStatement(INSERT_SQL);
                insertStmt.setString(1, wifiItem.getSSID());
                insertStmt.setString(2, wifiItem.getPassword());
                insertStmt.setString(3, wifiItem.getDescription());
                insertStmt.executeUpdate();
                System.out.println("Ho inserito la WifiItem " + item.getDescription() + " nel database.");
            } else {
                System.out.println("Ho aggiornato la WifiItem " + item.getDescription() + " nel database.");
            }
        } catch (SQLException e) {
            System.err.println("Errore nel salvataggio/aggiornamento di WifiItem: " + e.getMessage());
            throw new RuntimeException("Impossibile salvare/aggiornare WifiItem nel DB.", e);
        }
    }

    @Override
    public List<Item> carica() {
        List<Item> items = new ArrayList<>();

        try (Connection conn = DbConnector.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(SELECT_ALL_SQL)) {

            while (rs.next()) {
                String description = rs.getString("description");
                String ssid = rs.getString("ssid");
                String password = rs.getString("password");

                WifiItem wifiItem = new WifiItem(description, ssid, password);
                items.add(wifiItem);
            }
            System.out.println("Ho letto i dati dal database.");

        } catch (SQLException e) {
            System.err.println("Errore nel caricamento di WifiItem: " + e.getMessage());
            throw new RuntimeException("Impossibile caricare WifiItem dal DB.", e);
        }
        return items;
    }
}
