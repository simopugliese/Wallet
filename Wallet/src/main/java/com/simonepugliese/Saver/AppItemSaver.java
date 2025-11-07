package com.simonepugliese.Saver;

import com.simonepugliese.Item.AppItem;
import com.simonepugliese.Item.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AppItemSaver extends Saver{
    private static final String TABLE_NAME = "Apps";
    private static final String UPDATE_SQL =
            "UPDATE " + TABLE_NAME + " SET username = ?, password = ?, pin = ?, note = ? WHERE description = ?";
    private static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (username, password, pin, note, description) VALUES (?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    @Override
    public void salva(Item item) {
        AppItem appItem = (AppItem) item;

        try (Connection conn = DbConnector.getInstance().getConnection()) {
            PreparedStatement updateStmt = conn.prepareStatement(UPDATE_SQL);
            updateStmt.setString(1, appItem.getUsername());
            updateStmt.setString(2, appItem.getPassword());
            updateStmt.setString(3, appItem.getPin());
            updateStmt.setString(4, appItem.getNote());
            updateStmt.setString(5, appItem.getDescription());

            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows == 0) {
                PreparedStatement insertStmt = conn.prepareStatement(INSERT_SQL);
                insertStmt.setString(1, appItem.getUsername());
                insertStmt.setString(2, appItem.getPassword());
                insertStmt.setString(3, appItem.getPin());
                insertStmt.setString(4, appItem.getNote());
                insertStmt.setString(5, appItem.getDescription());
                insertStmt.executeUpdate();
                System.out.println("Ho inserito la AppItem " + item.getDescription() + " nel database.");
            } else {
                System.out.println("Ho aggiornato la AppItem " + item.getDescription() + " nel database.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Errore nel salvataggio/aggiornamento di AppItem: " + e.getMessage());
            throw new RuntimeException("Impossibile salvare/aggiornare AppItem nel DB.", e);
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
                String username = rs.getString("username");
                String password = rs.getString("password");
                String pin = rs.getString("pin");
                String note = rs.getString("note");

                AppItem appItem = new AppItem(description, username, password, pin, note);
                items.add(appItem);
            }
            System.out.println("Ho letto i dati dal database.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Errore nel caricamento di AppItem: " + e.getMessage());
            throw new RuntimeException("Impossibile caricare AppItem dal DB.", e);
        }
        return items;
    }
}
