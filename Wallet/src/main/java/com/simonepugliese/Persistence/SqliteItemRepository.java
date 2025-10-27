package com.simonepugliese.Persistence;

import com.simonepugliese.Data.*;
import com.simonepugliese.Logic.ItemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqliteItemRepository implements ItemRepository {
    private static volatile SqliteItemRepository instance;

    private final String url = "jdbc:sqlite:wallet.db";
    private Connection conn;

    private SqliteItemRepository() {
        try {
            this.conn = DriverManager.getConnection(url);
            if (this.conn != null) {
                System.out.println("Connessione a SQLite stabilita.");
                inizializeDB();
            }
        } catch (SQLException e) {
            System.err.println("Errore connessione DB: " + e.getMessage());
        }
    }

    public static SqliteItemRepository getInstance() {
        if (instance == null) {
            synchronized (SqliteItemRepository.class) {
                if (instance == null) {
                    instance = new SqliteItemRepository();
                }
            }
        }
        return instance;
    }

    @Override
    public void inizializeDB() {
        String sqlUsers = "CREATE TABLE IF NOT EXISTS Users ("
                + " username TEXT PRIMARY KEY,"
                + " password TEXT,"
                + " salt TEXT"
                + ");";

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
            stmt.execute(sqlUsers);
            stmt.execute(sqlLoginsBasic);
            stmt.execute(sqlCreditCards);
            stmt.execute(sqlWifisBasic);
            System.out.println("Tabelle controllate/create con successo.");
        } catch (SQLException e) {
            System.err.println("Errore creazione tabelle: " + e.getMessage());
        }
    }

    @Override
    public void saveUser(String username, byte[] password, byte[] salt) {
        String sql = "INSERT OR REPLACE INTO Users (username, password, salt) "
                + "VALUES(?, ?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            pstmt.setString(2, Arrays.toString(password));
            pstmt.setString(3, Arrays.toString(salt));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore salvataggio User: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public List<Object> loadUser(String username) {
        String sql = "SELECT * FROM Users";
        List<Object> user = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                user.add(rs.getString("username"));
                user.add(Arrays.copyOf(rs.getString("password").getBytes(), rs.getString("password").getBytes().length));
                user.add(Arrays.copyOf(rs.getString("salt").getBytes(), rs.getString("salt").getBytes().length));
            }
        } catch (SQLException e) {
            System.err.println("Errore caricamento Logins: " + e.getMessage());
        }
        return user;
    }


    @Override
    public void saveLoginBasic(LoginBasicItem login) {
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
    public List<LoginBasicItem> loadAllLoginBasic() {
        String sql = "SELECT * FROM LoginsBasic";
        List<LoginBasicItem> logins = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                LoginBasicItem login = new LoginBasicItem(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
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
    public void saveCreditCard(CreditCardItem carta) {
        String sql = "INSERT OR REPLACE INTO CreditCards (id, owner, bank, number, cvv, expiration, note) "
                + "VALUES(?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, carta.getId());
            pstmt.setString(2, carta.getOwner());
            pstmt.setString(3, carta.getBank());
            pstmt.setString(4, carta.getNumber());
            pstmt.setInt(5, carta.getCvv());
            pstmt.setString(6, carta.getExpiration());
            pstmt.setString(7, carta.getNote());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore salvataggio Carta: " + e.getMessage());
        }
    }

    @Override
    public List<CreditCardItem> loadAllCreditCards() {
        String sql = "SELECT * FROM CreditCards";
        List<CreditCardItem> carte = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                CreditCardItem carta = new CreditCardItem(
                        rs.getInt("id"),
                        rs.getString("owner"),
                        rs.getString("bank"),
                        rs.getString("number"),
                        rs.getInt("cvv"),
                        rs.getString("expiration"),
                        rs.getString("note")
                );
                carte.add(carta);
            }
        } catch (SQLException e) {
            System.err.println("Errore caricamento Carte: " + e.getMessage());
        }
        return carte;
    }

    @Override
    public void saveWifiBasic(WifiBasicItem wifi) {
        String sql = "INSERT OR REPLACE INTO WifisBasic (id, ssid, password, note) "
                + "VALUES(?, ?, ?, ?)";

        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, wifi.getId());
            pstmt.setString(2, wifi.getSsid());
            pstmt.setString(3, wifi.getPassword());
            pstmt.setString(4, wifi.getNote());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Errore salvataggio Wifi: " + e.getMessage());
        }
    }

    @Override
    public List<WifiBasicItem> loadAllWifisBasic() {
        String sql = "SELECT * FROM WifisBasic";
        List<WifiBasicItem> wifiList = new ArrayList<>();

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                WifiBasicItem wifi = new WifiBasicItem(
                        rs.getInt("id"),
                        rs.getString("ssid"),
                        rs.getString("password"),
                        rs.getString("note")
                );
                wifiList.add(wifi);
            }
        } catch (SQLException e) {
            System.err.println("Errore caricamento Wifi: " + e.getMessage());
        }
        return wifiList;
    }

    @Override
    public void deleteItem(int id, ItemType type) {
        String tabella;
        switch (type) {
            case LOGINBASIC:
                tabella = "LoginsBasic";
                break;
            case CREDITCARD:
                tabella = "CreditCards";
                break;
            case WIFIBASIC:
                tabella = "WifisBasic";
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