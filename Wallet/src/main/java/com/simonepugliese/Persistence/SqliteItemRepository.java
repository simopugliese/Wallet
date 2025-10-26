package com.simonepugliese.Persistence;

import com.simonepugliese.Data.*;
import com.simonepugliese.Logic.ItemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SqliteItemRepository implements ItemRepository {

    private final String url = "jdbc:sqlite:wallet.db";

    private Connection conn;

    public SqliteItemRepository() {
        try {
            this.conn = DriverManager.getConnection(url);
            if (this.conn != null) {
                System.out.println("Connessione a SQLite stabilita.");
            }
        } catch (SQLException e) {
            System.err.println("Errore connessione DB: " + e.getMessage());
        }
    }

    @Override
    public void inizializzaDB() {
        String sqlLoginsBasic = "CREATE TABLE IF NOT EXISTS LoginsBasic ("
                + " id INTEGER PRIMARY KEY,"
                + " username TEXT NOT NULL,"
                + " password TEXT,"
                + " urlSito TEXT,"
                + " note TEXT"
                + ");";

        String sqlCreditCards = "CREATE TABLE IF NOT EXISTS CreditCards ("
                + " id INTEGER PRIMARY KEY,"
                + " owner TEXT,"
                + " bank TEXT,"
                + " number TEXT,"
                + " cvv INTEGER,"
                + " expiration TEXT,"
                + " note TEXT"
                + ");";

        String sqlWifisBasic = "CREATE TABLE IF NOT EXISTS WifisBasic ("
                + " id INTEGER PRIMARY KEY,"
                + " ssid TEXT NOT NULL,"
                + " password TEXT,"
                + " note TEXT"
                + ");";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(sqlLoginsBasic);
            stmt.execute(sqlCreditCards);
            stmt.execute(sqlWifisBasic);
            System.out.println("Tabelle controllate/create con successo.");
        } catch (SQLException e) {
            System.err.println("Errore creazione tabelle: " + e.getMessage());
        }
    }

    @Override
    public void salvaLoginsBasic(LoginBasicItem login) {
        String sql = "INSERT OR REPLACE INTO LoginsBasic (id, username, password, urlSito, note) "
                + "VALUES(?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, login.getId());
            pstmt.setString(2, login.getUsername());
            pstmt.setString(3, login.getPassword());
            pstmt.setString(4, login.getUrlSito());
            pstmt.setString(5, login.getNote());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore salvataggio Login: " + e.getMessage());
        }
    }

    @Override
    public List<LoginBasicItem> caricaTuttiLoginBasic() {
        String sql = "SELECT * FROM LoginsBasic";
        List<LoginBasicItem> logins = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LoginBasicItem login = new LoginBasicItem(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"), // La Logica (Libreria 3) dovrà decifrarla
                        rs.getString("urlSito"),
                        rs.getString("note")
                );
                logins.add(login);
            }
        } catch (SQLException e) {
            System.err.println("Errore caricamento Logins: " + e.getMessage());
        }
        return logins;
    }

    @Override
    public void salvaCreditCards(CreditCardItem carta) {
        // DA IMPLEMENTARE:
        // String sql = "INSERT OR REPLACE INTO Carte (id, owner, bank, ...) VALUES(?, ?, ?, ...)";
        // ...
        System.out.println("Metodo salvaCarta non ancora implementato.");
    }

    @Override
    public List<CreditCardItem> caricaTutteCreditCards() {
        // DA IMPLEMENTARE:
        // String sql = "SELECT * FROM Carte";
        // ...
        System.out.println("Metodo caricaTutteCarte non ancora implementato.");
        return new ArrayList<>();
    }

    @Override
    public void salvaWifisBasic(WifiBasicItem wifi) {
        // DA IMPLEMENTARE:
        // String sql = "INSERT OR REPLACE INTO Wifi (id, ssid, password, note) VALUES(?, ?, ?, ?)";
        // ...
        System.out.println("Metodo salvaWifi non ancora implementato.");
    }

    @Override
    public List<WifiBasicItem> caricaTuttiWifisBasic() {
        // DA IMPLEMENTARE:
        // String sql = "SELECT * FROM Wifi";
        // ...
        System.out.println("Metodo caricaTuttiWifi non ancora implementato.");
        return new ArrayList<>();
    }

    @Override
    public void deleteItem(int id, ItemType type) {
        String tabella;
        switch (type) {
            case LOGINBASIC:
                tabella = "Logins";
                break;
            case CREDITCARD:
                tabella = "Carte";
                break;
            case WIFIBASIC:
                tabella = "Wifi";
                break;
            default:
                System.err.println("Tipo non supportato per eliminazione.");
                return;
        }

        String sql = "DELETE FROM " + tabella + " WHERE id = ?";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore eliminazione " + type + ": " + e.getMessage());
        }
    }
}
