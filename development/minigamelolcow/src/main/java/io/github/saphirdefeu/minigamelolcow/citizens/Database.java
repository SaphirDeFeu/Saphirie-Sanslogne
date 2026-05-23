package io.github.saphirdefeu.minigamelolcow.citizens;

import io.github.saphirdefeu.minigamelolcow.Logger;
import io.github.saphirdefeu.minigamelolcow.Main;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.*;
import java.util.LinkedList;

/**
 * Classe permettant de gérer la base de données du plugin située à `plugins/MinigameLolCow/citizens.db`
 */
public abstract class Database {
    protected static Connection connection;

    /**
     * Ouvre une connection à la base de données `citizens.db`
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

        Path directoryPath = Main.getPluginDataFolder().toPath();
        Path filePath = directoryPath.resolve("citizens.db").normalize();

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
            connection = DriverManager.getConnection("jdbc:sqlite:plugins/minigamelolcow/citizens.db");
            String sql = "CREATE TABLE IF NOT EXISTS citizens (" +
                    "id TEXT PRIMARY KEY NOT NULL," +
                    "firstname TEXT NOT NULL," +
                    "lastname TEXT NOT NULL," +
                    "birthplace TEXT NOT NULL," +
                    "birthdate TEXT NOT NULL," +
                    "state TEXT NOT NULL," +
                    "gender TEXT NOT NULL," +
                    "height TEXT NOT NULL," +
                    "job TEXT NOT NULL," +
                    "livingplace TEXT NOT NULL," +
                    "notes TEXT NOT NULL," +
                    "cidmid TEXT NOT NULL," +
                    "skinmid TEXT NOT NULL" +
                    ");";
            try (Statement statement = connection.createStatement()) {
                statement.execute(sql);
            }
        } catch (SQLException e) {
            Logger.err(String.format("SQLException: cannot connect to database 'minigamelolcow/citizens.db' or attempt to create statement failed.\n%s", e));
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
     * Génères une nouvelle ID citoyenne
     * Cela signifie: ABC1234567*!
     * * est le reste mod 10 de la multiplication des 7 chiffres précédents
     * ! est le reste mod 10 de la somme des 7 chiffres précédents
     * @return une ID
     */
    public static String generateCitizenId() {
        StringBuilder id = new StringBuilder();
        for(int i = 0; i < 3; i++) {
            int random = (int)(Math.random() * 26) + 65; // 65 to 90 (A to Z)
            id.append((char) random);
        }

        int mul = 1;
        int sum = 0;

        for(int i = 0; i < 7; i++) {
            int random = (int)(Math.random() * 9) + 1; // 1 to 9
            mul *= random;
            sum += random;
            id.append(random);
        }

        int res_mul = mul % 10;
        int res_sum = sum % 10;

        id.append(res_mul);
        id.append(res_sum);

        return id.toString();
    }

    public static boolean isValidID(String id) {
        for(int i = 0; i < 3; i++) {
            char c = id.charAt(i);
            if((int) c > 90 || (int) c < 65) return false;
        }

        int mul = 1;
        int sum = 0;
        for(int i = 3; i < id.length() - 2; i++) {
            char c = id.charAt(i);
            if((int) c > 57 || (int) c < 49) return false;
            int v = ((int) c) - 48; // transform char to its corresponding integer value
            mul *= v;
            sum += v;
        }

        int checkmul = ((int) id.charAt(10)) - 48;
        int checksum = ((int) id.charAt(11)) - 48;

        return (mul % 10 == checkmul) && (sum % 10 == checksum);
    }

    /**
     * Crée un nouveau compte
     * @return l'id du compte, sinon 0
     */
    public static String addEntry() {
        String id = "";
        String sql = "SELECT COUNT(*) FROM citizens WHERE id = ?;";

        // Check si le compte existe déjà
        // si oui, alors juste on génère une nouvelle id et on prie que ça soit pas déjà pris.
        // oui je sais c'est dégueulasse
        while(true) {
            id = generateCitizenId();
            try(PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, id); // Le premier "?" du texte est remplacé par l'id
                try(ResultSet rs = statement.executeQuery()) {
                    if(rs.next()) {
                        int count = rs.getInt(1);
                        if(count > 0) {
                            Logger.warn("Trying to create account with username already existing");
                        } else {
                            break;
                        }
                    }
                }
            } catch (SQLException e) {
                Logger.err(String.format("SQLException: attempt to create statement failed.\n%s", e));
                return "0";
            }
        }

        // Créer le compte avec 100$ de salaire et de compte
        sql = "INSERT OR IGNORE INTO citizens VALUES (?, \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\", \"\");";
        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id); // Le premier "?" du texte est remplacé par username
            int rs = statement.executeUpdate();
            if(rs > 0) {
                Logger.debug("Successfully created account for citizen '" + id + "'");
            }
        } catch (SQLException e) {
            Logger.err(String.format("SQLException: attempt to create statement failed.\n%s", e));
            return "0";
        }

        return id;
    }

    /**
     * Supprimes le compte avec une clef
     * @param id l'identifiant du citoyen
     * @return 0 - succès | 1 - erreur
     */
    public static int deleteAccount(@NotNull String id) {
        String sql = "DELETE FROM citizens WHERE id = ?;";

        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id);
            int rowsDeleted = statement.executeUpdate();
        } catch (SQLException e) {
            Logger.err(String.format("SQLException:\n%s", e));
            return 1;
        }

        return 0;
    }

    /**
     * Récupères toutes les données d'un citoyen
     * @param id id citoyenne
     * @return Les données du citoyen. si le champ id est *, alors ce citoyen n'existe pas
     * @throws RuntimeException si java.sql ne peut pas exécuter la requête
     */
    public static CitizenSearchQuery getDataFromId(String id) {
        String sql = "SELECT * FROM citizens WHERE id = ?;";
        CitizenSearchQuery data = new CitizenSearchQuery();
        data.id = id;

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setString(1, id); // Remplaces le premier ? par `username`
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    data.firstname = rs.getString("firstname");
                    data.lastname = rs.getString("lastname");
                    data.birthdate = rs.getString("birthdate");
                    data.birthplace = rs.getString("birthplace");
                    data.gender = rs.getString("gender");
                    data.height = rs.getString("height");
                    data.job = rs.getString("job");
                    data.state = rs.getString("state");
                    data.livingplace = rs.getString("livingplace");
                    data.notes = rs.getString("notes");
                    data.cidmid = rs.getString("cidmid");
                    data.skinmid = rs.getString("skinmid");
                } else {
                    data.id = "*";
                }
            }
        } catch (SQLException e) {
            Logger.err(String.format("%s", e));
            throw new RuntimeException(e);
        }

        return data;
    }

    /**
     * Modifies les données
     * @param id id citoyenne
     * @param data les données à modifier (null si ne doit pas être modifié)
     *
     * @throws RuntimeException si java.sql ne peut pas exécuter la requête
     */
    public static int setDataFromId(String id, CitizenSearchQuery data) {
        String[] parameters = new String[12];
        int lastIndex = 0;

        StringBuilder sql = new StringBuilder("UPDATE citizens SET ");

        if(data.firstname != null) { sql.append("firstname = ?, "); parameters[lastIndex] = data.firstname; lastIndex++; }
        if(data.lastname != null) { sql.append("lastname = ?, "); parameters[lastIndex] = data.lastname; lastIndex++; }
        if(data.birthplace != null) { sql.append("birthplace = ?, "); parameters[lastIndex] = data.birthplace; lastIndex++; }
        if(data.birthdate != null) { sql.append("birthdate = ?, "); parameters[lastIndex] = data.birthdate; lastIndex++; }
        if(data.state != null) { sql.append("state = ?, "); parameters[lastIndex] = data.state; lastIndex++; }
        if(data.gender != null) { sql.append("gender = ?, "); parameters[lastIndex] = data.gender; lastIndex++; }
        if(data.height != null) { sql.append("height = ?, "); parameters[lastIndex] = data.height; lastIndex++; }
        if(data.job != null) { sql.append("job = ?, "); parameters[lastIndex] = data.job; lastIndex++; }
        if(data.livingplace != null) { sql.append("livingplace = ?, "); parameters[lastIndex] = data.livingplace; lastIndex++; }
        if(data.notes != null) { sql.append("notes = ?, "); parameters[lastIndex] = data.notes; lastIndex++; }
        if(data.cidmid != null) { sql.append("cidmid = ?, "); parameters[lastIndex] = data.cidmid; lastIndex++; }
        if(data.skinmid != null) { sql.append("firstname = ?, "); parameters[lastIndex] = data.skinmid; lastIndex++; }

        sql.append("id = ? WHERE id = ?");

        try(PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for(int i = 0; i < parameters.length; i++) {
                if(parameters[i] == null) break;
                statement.setString(i + 1, parameters[i]);
            }
            statement.setString(lastIndex + 1, id);
            statement.setString(lastIndex + 2, id);
            int rows = statement.executeUpdate();
            if(rows > 0) {
                Logger.debug(String.format("Updated data of %s", id));
                return 0;
            }
        } catch (SQLException e) {
            Logger.err(String.format("%s", e));
            return 1;
        }

        return 0;
    }

    /**
     * Renvoies une liste de potentielles id citoyennes selon une recherche
     * @param searchQuery paramètres de recherche. ne check pas l'id
     * @returns une liste d'id citoyennes potentielles
     */
    public static LinkedList<String> getIdsFromSearchQuery(CitizenSearchQuery searchQuery) {
        String[] parameters = new String[12];
        int lastIndex = 0;

        StringBuilder sql = new StringBuilder("SELECT id FROM citizens WHERE ");
        String and = "";

        LinkedList<String> ids = new LinkedList<>();

        if(searchQuery.firstname != null) { sql.append("firstname = ?"); parameters[lastIndex] = searchQuery.firstname; lastIndex++; and = " AND "; }
        if(searchQuery.lastname != null) { sql.append(and).append("lastname = ?"); parameters[lastIndex] = searchQuery.lastname; lastIndex++; and = " AND "; }
        if(searchQuery.birthplace != null) { sql.append(and).append("birthplace = ?"); parameters[lastIndex] = searchQuery.birthplace; lastIndex++; and = " AND "; }
        if(searchQuery.birthdate != null) { sql.append(and).append("birthdate = ?"); parameters[lastIndex] = searchQuery.birthdate; lastIndex++; and = " AND "; }
        if(searchQuery.state != null) { sql.append(and).append("state = ?"); parameters[lastIndex] = searchQuery.state; lastIndex++; and = " AND "; }
        if(searchQuery.gender != null) { sql.append(and).append("gender = ?"); parameters[lastIndex] = searchQuery.gender; lastIndex++; and = " AND "; }
        if(searchQuery.height != null) { sql.append(and).append("height = ?"); parameters[lastIndex] = searchQuery.height; lastIndex++; and = " AND "; }
        if(searchQuery.job != null) { sql.append(and).append("job = ?"); parameters[lastIndex] = searchQuery.job; lastIndex++; and = " AND "; }
        if(searchQuery.livingplace != null) { sql.append(and).append("livingplace = ?"); parameters[lastIndex] = searchQuery.livingplace; lastIndex++; and = " AND "; }
        if(searchQuery.notes != null) { sql.append(and).append("notes = ?"); parameters[lastIndex] = searchQuery.notes; lastIndex++; and = " AND "; }
        if(searchQuery.cidmid != null) { sql.append(and).append("cidmid = ?"); parameters[lastIndex] = searchQuery.cidmid; lastIndex++; and = " AND "; }
        if(searchQuery.skinmid != null) { sql.append(and).append("skinmid = ?"); parameters[lastIndex] = searchQuery.skinmid; lastIndex++; }

        try(PreparedStatement statement = connection.prepareStatement(sql.toString())) {
            for(int i = 0; i < parameters.length; i++) {
                if(parameters[i] == null) break;
                statement.setString(i + 1, parameters[i]);
            }
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    ids.add(rs.getString(1));
                } else {
                    return ids;
                }
            }
        } catch (SQLException e) {
            Logger.err(String.format("%s", e));
            throw new RuntimeException(e);
        }

        return ids;
    }

    public static int getCount() {
        String sql = "SELECT COUNT(*) FROM citizens";

        try(PreparedStatement statement = connection.prepareStatement(sql)) {
            try(ResultSet rs = statement.executeQuery()) {
                if(rs.next()) {
                    int count = rs.getInt(1);
                    Logger.debug(String.format("count: %d", count));
                    return count;
                }
            }
        } catch (SQLException e) {
            Logger.err(String.format("%s", e));
            throw new RuntimeException(e);
        }

        return -1;
    }

    public static class CitizenSearchQuery {
        public String id = null;
        public String firstname = null;
        public String lastname = null;
        public String birthplace = null;
        public String birthdate = null;
        public String state = null;
        public String gender = null;
        public String height = null;
        public String job = null;
        public String livingplace = null;
        public String notes = null;
        public String cidmid = null;
        public String skinmid = null;

        public CitizenSearchQuery() {};

        public CitizenSearchQuery(
                String _id,
                String _firstname,
                String _lastname,
                String _birthplace,
                String _birthdate,
                String _state,
                String _gender,
                String _height,
                String _job,
                String _livingplace,
                String _notes,
                String _cidmid,
                String _skinmid
        ) {
            this.id = _id;
            this.firstname = _firstname;
            this.lastname = _lastname;
            this.birthplace = _birthplace;
            this.birthdate = _birthdate;
            this.state = _state;
            this.gender = _gender;
            this.height = _height;
            this.job = _job;
            this.livingplace = _livingplace;
            this.notes = _notes;
            this.cidmid = _cidmid;
            this.skinmid = _skinmid;
        }
    }
}
