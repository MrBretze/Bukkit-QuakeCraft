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
            "LastConnection DATETIME )";

    public static String SQL_CREATE_GAMES_TABLE = "CREATE TABLE Games ( " +
            "Name VARCHAR(128), " +
            "DisplayName VARCHAR(128), " +
            "FirstLocation VARCHAR(128), " +
            "SecondLocation VARCHAR(128), " +
            "SpawnLocation VARCHAR(128), " +
            "MaxPlayer INTEGER, " +
            "MinPlayer INTEGER, " +
            "MaxKill INTEGER, " +
            "Respawns VARCHAR(MAX), " +
            "Signs VARCHAR(MAX) )";

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

    public Object getObject(String colon, String where, Table table) {
        Object o = null;
        PreparedStatement statement = null;
        try {
            statement = openConnection().prepareStatement("SELECT " + colon + " FROM " + table.getTable() + " " + where);
            ResultSet set = statement.executeQuery();
            o = set.next() ? set.getObject(1) : null;
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (getConfigType() == Type.LOCAL_SQL) {
                try {
                    if (statement != null)
                        statement.close();
                    getConfig().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return o;
    }

    public String getString(String colon, String where, Table table) {
        return (String) getObject(colon, where, table);
    }

    public int getInt(String colon, String where, Table table) {
        return (int) getObject(colon, where, table);
    }

    public double getDouble(String colon, String where, Table table) {
        return (double) getObject(colon, where, table);
    }

    private String queryBaseSet = "INSERT INTO %table% ( %colon% ) VALUES (?) %where%";

    public void setObject(Object value, String colon, String where, Table table) {
        try {
            PreparedStatement statement = openConnection().prepareStatement(queryBaseSet.replace("%table%", table.getTable()).replace("%colon%", colon).replace("%where%", where));
            statement.setObject(1, value);
            statement.executeUpdate();
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (getConfigType() == Type.LOCAL_SQL) {
                try {
                    getConfig().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public void setString(String value, String colon, String where, Table table) {
        setObject(value, colon, where, table);
    }

    public void setInt(int value, String colon, String where, Table table) {
        setObject(value, colon, where, table);
    }

    public void setDouble(double value, String colon, String where, Table table) {
        setObject(value, colon, where, table);
    }

    public void executePreparedStatement(PreparedStatement statement) {
        try {
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            if (getConfigType() == Type.LOCAL_SQL) {
                try {
                    statement.close();
                    getConfig().getConnection().close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface IConfig {
        IConfig init(Config config) throws SQLException, ClassNotFoundException;

        Connection openConnection() throws ClassNotFoundException, SQLException;

        Connection getConnection();
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