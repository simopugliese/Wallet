package com.simonepugliese.Saver;

import com.simonepugliese.Item.Item;
import com.simonepugliese.Item.LoginItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoginItemSaver extends Saver {

    private static final String TABLE_NAME = "LoginsBasic";
    private static final String UPDATE_SQL =
            "UPDATE " + TABLE_NAME + " SET username = ?, password = ?, urlSito = ? WHERE description = ?";
    private static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (username, password, urlSito, description) VALUES (?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    @Override
    public void salva(Item item) {
        LoginItem loginItem = (LoginItem) item;

        try (Connection conn = DbConnector.getInstance().getConnection()) {
            PreparedStatement updateStmt = conn.prepareStatement(UPDATE_SQL);
            updateStmt.setString(1, loginItem.getUsername());
            updateStmt.setString(2, loginItem.getPassword());
            updateStmt.setString(3, loginItem.getUrlSito());
            updateStmt.setString(4, loginItem.getDescription());

            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows == 0) {
                PreparedStatement insertStmt = conn.prepareStatement(INSERT_SQL);
                insertStmt.setString(1, loginItem.getUsername());
                insertStmt.setString(2, loginItem.getPassword());
                insertStmt.setString(3, loginItem.getUrlSito());
                insertStmt.setString(4, loginItem.getDescription());
                insertStmt.executeUpdate();
                System.out.println("Ho inserito la LoginItem " + item.getDescription() + " nel database.");
            } else {
                System.out.println("Ho aggiornato la LoginItem " + item.getDescription() + " nel database.");
            }
        } catch (SQLException e) {
            System.err.println("Errore nel salvataggio/aggiornamento di LoginItem: " + e.getMessage());
            throw new RuntimeException("Impossibile salvare/aggiornare LoginItem nel DB.", e);
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
                String urlSito = rs.getString("urlSito");

                LoginItem loginItem = new LoginItem(description, username, password, urlSito);
                items.add(loginItem);
            }
            System.out.println("Ho letto i dati dal database.");

        } catch (SQLException e) {
            System.err.println("Errore nel caricamento di LoginItem: " + e.getMessage());
            throw new RuntimeException("Impossibile caricare LoginItem dal DB.", e);
        }
        return items;
    }
}