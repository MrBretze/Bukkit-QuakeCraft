package fr.bretzel.quake.config;

import fr.bretzel.quake.config.local.LocalConfig;
import fr.bretzel.quake.config.online.OnlineConfig;

import java.io.File;
import java.sql.*;

public class Config {

    public static String SQL_CREATE_QUAKE_TABLE = "CREATE TABLE Players ( " +
            "UUID VARCHAR(42), " +
            "Effect VARCHAR(42), " +
            "Reload DOUBLE, " +
            "PlayerKill INTEGER, " +
            "Coins INTEGER, " +
            "Win INTEGER, " +
            "KillStreak INTEGER, " +
            "Death INTEGER," +
            "Name VARCHAR(42), " +
            "LastConnection DATE )";

    private Type configType;
    private IConfig config;

    public Config(String hostname, String username, String password, String port, String database) {
        this.configType = Type.ONLINE_SQL;

        try {
            config = new OnlineConfig(hostname, username, password, port, database).init(this);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Config(File conf) {
        this.configType = Type.LOCAL_SQL;
        try {
            config = new LocalConfig(conf).init(this);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public Connection openConnection() {
        try {
            return config.openConnection();
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean ifStringExist(String value, String colon, Table table) {
        try {
            Statement statement = openConnection().createStatement();
            String query = "SELECT " + colon + " FROM " + table.getTable() + " WHERE " + colon + " = '" + value + "'";
            ResultSet set = statement.executeQuery(query);
            if (set.next()) {
                String v = set.getString(colon);
                if (v.equalsIgnoreCase(value))
                    return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Type getConfigType() {
        return configType;
    }

    private IConfig getConfig() {
        return config;
    }

    public String getString(String colon, String where, Table table) {
        return getConfig().getString("SELECT " + colon + " FROM " + table.getTable() + " " + where);
    }

    public int getInt(String colon, String where, Table table) {
        return getConfig().getInt("SELECT " + colon + " FROM " + table.getTable() + " " + where);
    }

    public double getDouble(String colon, String where, Table table) {
        return getConfig().getDouble("SELECT " + colon + " FROM " + table.getTable() + " " + where);
    }

    private String queryBaseSet = "INSERT INTO %table% (%colon%) VALUES (?) %where%";

    public void setString(String value, String colon, String where, Table table) {
        getConfig().setString(queryBaseSet.replace("%table%", table.getTable()).replace("(%colon%)", "(" + colon + ")").replace("%where%", where), value);
    }

    public void setInt(int value, String colon, String where, Table table) {
        getConfig().setInt(queryBaseSet.replace("%table%", table.getTable()).replace("(%colon%)", "(" + colon + ")").replace("%where%", where), value);
    }

    public void setDouble(double value, String colon, String where, Table table) {
        getConfig().setDouble(queryBaseSet.replace("%table%", table.getTable()).replace("(%colon%)", "(" + colon + ")").replace("%where%", where), value);
    }

    public void executePreparedStatement(PreparedStatement statement) {
        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public interface IConfig {
        IConfig init(Config config) throws SQLException, ClassNotFoundException;

        String getString(String query);

        int getInt(String query);

        double getDouble(String query);

        void setString(String query, String value);

        void setInt(String query, int value);

        void setDouble(String query, double value);

        Connection openConnection() throws ClassNotFoundException, SQLException;
    }

    public enum Table {
        PLAYERS("Players"),
        TEST("Test");

        private String table;

        Table(String table) {
            this.table = table;
        }

        public String getTable() {
            return table;
        }
    }

    public enum Type {
        LOCAL_SQL,
        ONLINE_SQL
    }
}



/**
 *
 * CREATE TABLE `QuakeCraft`.`Players` ( `UUID` VARCHAR(36) NULL DEFAULT NULL COMMENT 'UUID Of MC Player' , `Effect` VARCHAR(20) NOT NULL DEFAULT 'fireworksSpark' COMMENT 'Effect player shoot' , `Reload` VARCHAR(5) NOT NULL DEFAULT '1.5' COMMENT 'Reload time for player' , `PlayerKill` INT NOT NULL DEFAULT '0' COMMENT 'Number of player Kill' , `Coins` INT NOT NULL DEFAULT '0' COMMENT 'Coins of Player' , `Won` INT NOT NULL DEFAULT '0' COMMENT 'Number of win' , `KillStreak` INT NOT NULL DEFAULT '0' COMMENT 'Number of KillStreak' , `Death` INT NOT NULL DEFAULT '0' COMMENT 'Death' ) ENGINE = InnoDB;
 *
 *
 */