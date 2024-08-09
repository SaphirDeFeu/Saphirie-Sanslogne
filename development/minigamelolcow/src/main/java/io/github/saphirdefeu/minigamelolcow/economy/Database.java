package io.github.saphirdefeu.minigamelolcow.economy;

import io.github.saphirdefeu.minigamelolcow.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.HashMap;

/**
 * Classe permettant de gérer la base de données du plugin située à `plugins/MinigameLolCow/balances.db`
 */
public abstract class Database {
    protected static Connection connection;

    /**
     * Ouvre une connection à la base de données `balances.db`
     * @return 0 - succès | 1 - erreur
     */
    public static int connect() {
        // Ouvrir une connection à la base de données
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            Logger.err(String.format("ClassNotFoundException: cannot find class 'org.sqlite.JDBC'\n%s", e));
            return 1;
        }

        Path directoryPath = Paths.get("plugins/MinigameLolCow");
        Path filePath = directoryPath.resolve("balances.db");

        // Si elle n'existe pas, on en crée une nouvelle
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

        // Créer une table pour enregistrer les comptes
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

    /**
     * Fermes la connection à la base de données
     */
    public static void close() {
        try {
            connection.close();
        } catch (SQLException e) {
            Logger.err("Error while closing the database connection - You may safely disregard this error message as the JVM will release the connection by itself.");
        }
    }

    /**
     * Créer un compte du nom `username` (si il n'en existe pas) avec 100.0$ en balance et salaire
     * @param username Le nom du compte
     * @return 0 - succès | 1 - erreur
     */
    public static int createAccountHolder(String username) {
        String sql = "SELECT COUNT(*) FROM accounts WHERE username = ?;";

        // Check si le compte existe déjà
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username); // Le premier "?" du texte est remplacé par username
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

        // Créer le compte avec 100$ de salaire et de compte
        sql = "INSERT OR REPLACE INTO ACCOUNTS (username, balance, salary) VALUES (?, 100.0, 100.0);";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username); // Le premier "?" du texte est remplacé par username
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

    /**
     * Supprimes le compte de nom `username`
     * @param username Le nom du compte à supprimer
     * @return 0 - succès | 1 - erreur
     */
    public static int deleteAccount(@NotNull String username) {
        String sql = "DELETE FROM accounts WHERE username = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username);
            int rowsDeleted = statement.executeUpdate();
        } catch (SQLException e) {
            Logger.err(String.format("SQLException:\n%s", e));
            return 1;
        }

        return 0;
    }

    /**
     * Récupères la balance d'un compte
     * @param username Le nom du compte
     * @return La balance du compte
     * @throws RuntimeException si java.sql ne peut pas exécuter la requête
     */
    public static double getAccountBalance(String username) {
        String sql = "SELECT balance FROM accounts WHERE username = ?;";
        double balance = 0.0;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username); // Remplaces le premier ? par `username`
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

    /**
     * Spécifies la balance d'un compte à une nouvelle balance
     * @param username Le nom du compte
     * @param balance La nouvelle balance
     *
     * @throws RuntimeException si java.sql ne peut pas exécuter la requête
     */
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

    /**
     * Récupères tous les comptes
     * @return Une table de comptes avec noms comme clés et balances comme valeurs
     */
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

    /**
     * Spécifies le salaire d'un compte
     * @param username Le nom du compte
     * @param salary Le nouveau salaire
     */
    public static void setAccountSalary(String username, double salary) {
        String sql = "UPDATE ACCOUNTS SET salary = ? WHERE username = ?;";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setDouble(1, salary); // Le premier "?" du texte est remplacé par salary
            statement.setString(2, username); // Le deuxième "?" du texte est remplacé par username
            int rows = statement.executeUpdate();
            if(rows > 0) {
                Logger.debug(String.format("Updated salary of %s to %.2f", username, salary));
            }
        } catch (SQLException e) {
            Logger.err(String.format("%s", e));
            throw new RuntimeException(e);
        }
    }

    /**
     * Récupères le salaire d'un compte
     * @param username Le nom du compte
     * @return Le salaire du compte
     */
    public static double getAccountSalary(String username) {
        String sql = "SELECT salary FROM accounts WHERE username = ?;";
        double salary = 0.0;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, username); // Le premier "?" du texte est remplacé par username
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
