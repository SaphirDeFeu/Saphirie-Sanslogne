package io.github.saphirdefeu.minigamelolcow.economy;

import io.github.saphirdefeu.minigamelolcow.Logger;
import org.bukkit.Bukkit;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;

public abstract class Database {
    /*public Database() {
        final String cmd = String.format("scoreboard objectives add %s dummy {\"text\":\"%s\"}", EconomyAddon.ECONOMY_DATA_RESOURCE, EconomyAddon.RESOURCE_LOCATION);
        final boolean dataReturnValue = Bukkit.dispatchCommand(EconomyAddon.consoleSender, cmd);
    }*/

    protected static Connection connection;

    public static int connect() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Logger.err(String.format("ClassNotFoundException: cannot find class 'org.sqlite.JDBC'\n%s", e));
            return 1;
        }

        Path directoryPath = Paths.get("plugins/MinigameLolCow");
        Path filePath = directoryPath.resolve("balances.db");

        try {
            if (!Files.exists(directoryPath)) {
                Files.createDirectories(directoryPath);
            }

            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            Logger.err(String.format("IOException: cannot create directories or file '%s'\n%s", filePath, e));
            return 1;
        }

        try {
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/MinigameLolCow/balances.db");
            String sql = "CREATE TABLE IF NOT EXISTS accounts (" +
                    "username TEXT PRIMARY KEY NOT NULL," +
                    "balance REAL NOT NULL," +
                    "salary REAL NOT NULL" +
                    ");";
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            Logger.err(String.format("SQLException: cannot connect to database 'MinigameLolCow/balances.db' or attempt to create statement failed.\n%s", e));
            return 1;
        }

        return 0;
    }

    public static int close() {
        try {
            connection.close();
        } catch (SQLException e) {
            Logger.err("Error while closing the database connection - You may safely disregard this error message as the JVM will release the connection by itself.");
            return 1;
        }
        return 0;
    }

    public static int createAccountHolder(String username) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE username = ?;";

        // Check if account already exists
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    int count = rs.getInt(1);
                    if(count > 0) {
                        Logger.warn("Trying to create account with username already existing");
                        return 1;
                    }
                }
            }
        } catch (SQLException e) {
            Logger.err(String.format("SQLException: attempt to create statement failed.\n%s", e));
            return 1;
        }

        sql = "INSERT OR REPLACE INTO ACCOUNTS (username, balance, salary) VALUES (?, 100.0, 100.0);";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            int rs = statement.executeUpdate();
            if(rs > 0) {
                Logger.debug("Successfully created account for player '" + username + "'");
            }
        } catch (SQLException e) {
            Logger.err(String.format("SQLException: attempt to create statement failed.\n%s", e));
            return 1;
        }

        return 0;
    }

    public static int deleteAccount(String username) {
        String sql = "DELETE FROM accounts WHERE username = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            int rowsDeleted = statement.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQLException: " + e);
            return 1;
        }

        return 0;
    }

    public static double getAccountBalance(String username) {
        String sql = "SELECT balance FROM accounts WHERE username = ?;";
        double balance = 0.0;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    balance = rs.getDouble("balance");
                }
            }
        } catch (SQLException e) {
            Logger.err(String.format("%s", e));
            throw new RuntimeException(e);
        }

        return balance;
    }

    public static void setAccountBalance(String username, double balance) {
        String sql = "UPDATE ACCOUNTS SET balance = ? WHERE username = ?;";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, balance);
            statement.setString(2, username);
            int rows = statement.executeUpdate();
            if(rows > 0) {
                Logger.debug(String.format("Updated balance of %s to %.2f", username, balance));
            }
        } catch (SQLException e) {
            Logger.err(String.format("%s", e));
            throw new RuntimeException(e);
        }
    }

    public static HashMap<String, Double> getAccounts() {
        String sql = "SELECT * FROM accounts;";
        HashMap<String, Double> accounts = new HashMap<>();

        try (PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet rs = statement.executeQuery()) {

            while (rs.next()) {
                String username = rs.getString("username");
                double balance = rs.getDouble("balance");
                accounts.put(username, balance);
            }
        } catch (SQLException e) {
            Logger.err("SQLException: " + e);
            throw new RuntimeException(e);
        }

        return accounts;
    }

    public static void updateSalary(String username, double salary) {
        String sql = "UPDATE ACCOUNTS SET salary = ? WHERE username = ?;";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, salary);
            statement.setString(2, username);
            int rows = statement.executeUpdate();
            if(rows > 0) {
                Logger.debug(String.format("Updated salary of %s to %.2f", username, salary));
            }
        } catch (SQLException e) {
            Logger.err(String.format("%s", e));
            throw new RuntimeException(e);
        }
    }

    public static double getAccountSalary(String username) {
        String sql = "SELECT salary FROM accounts WHERE username = ?;";
        double salary = 0.0;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    salary = rs.getDouble("salary");
                }
            }
        } catch (SQLException e) {
            Logger.err(String.format("%s", e));
            throw new RuntimeException(e);
        }

        return salary;
    }
}
