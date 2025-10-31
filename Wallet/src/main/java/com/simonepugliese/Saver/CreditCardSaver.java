package com.simonepugliese.Saver;

import com.simonepugliese.Item.CreditCardItem;
import com.simonepugliese.Item.Item;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CreditCardSaver extends Saver {

    private static final String TABLE_NAME = "CreditCards";
    private static final String UPDATE_SQL =
            "UPDATE " + TABLE_NAME + " SET owner = ?, bank = ?, number = ?, cvv = ?, expiration = ? WHERE description = ?";
    private static final String INSERT_SQL =
            "INSERT INTO " + TABLE_NAME + " (owner, bank, number, cvv, expiration, description) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_ALL_SQL =
            "SELECT * FROM " + TABLE_NAME;

    @Override
    public void salva(Item item) {
        CreditCardItem creditCardItem = (CreditCardItem) item;

        try (Connection conn = DbConnector.getInstance().getConnection()) {
            PreparedStatement updateStmt = conn.prepareStatement(UPDATE_SQL);
            updateStmt.setString(1, creditCardItem.getOwner());
            updateStmt.setString(2, creditCardItem.getBank());
            updateStmt.setString(3, creditCardItem.getNumber());
            updateStmt.setString(4, creditCardItem.getCvv());
            updateStmt.setString(5, creditCardItem.getExpiration());
            updateStmt.setString(6, creditCardItem.getDescription());

            int affectedRows = updateStmt.executeUpdate();
            if (affectedRows == 0) {
                PreparedStatement insertStmt = conn.prepareStatement(INSERT_SQL);
                insertStmt.setString(1, creditCardItem.getOwner());
                insertStmt.setString(2, creditCardItem.getBank());
                insertStmt.setString(3, creditCardItem.getNumber());
                insertStmt.setString(4, creditCardItem.getCvv());
                insertStmt.setString(5, creditCardItem.getExpiration());
                insertStmt.setString(6, creditCardItem.getDescription());
                insertStmt.executeUpdate();
                System.out.println("Ho inserito la CreditCardItem " + item.getDescription() + " nel database.");
            } else {
                System.out.println("Ho aggiornato la CreditCardItem " + item.getDescription() + " nel database.");
            }

        } catch (SQLException e) {
            System.err.println("Errore nel salvataggio/aggiornamento di CreditCardItem: " + e.getMessage());
            throw new RuntimeException("Impossibile salvare/aggiornare CreditCardItem nel DB.", e);
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
                String owner = rs.getString("owner");
                String bank = rs.getString("bank");
                String number = rs.getString("number");
                String cvv = rs.getString("cvv");
                String expiration = rs.getString("expiration");

                CreditCardItem creditCardItem = new CreditCardItem(description, owner, bank, number, cvv, expiration);
                items.add(creditCardItem);
            }
            System.out.println("Ho letto i dati dal database.");

        } catch (SQLException e) {
            System.err.println("Errore nel caricamento di CreditCardItem: " + e.getMessage());
            throw new RuntimeException("Impossibile caricare CreditCardItem dal DB.", e);
        }
        return items;
    }
}