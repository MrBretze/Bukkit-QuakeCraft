package fr.bretzel.quake.config.local;

import fr.bretzel.quake.config.Config;

import java.io.File;
import java.sql.*;

public class LocalConfig implements Config.IConfig {

    private Connection connection;
    private File db;

    public LocalConfig(File db) {
        this.db = db;
    }

    /**
     * Start set value to DB
     */


    @Override
    public void setString(String query, String value) {
        try {
            PreparedStatement statement = openConnection().prepareStatement(query);
            statement.setString(1, value);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setInt(String query, int value) {
        try {
            PreparedStatement statement = openConnection().prepareStatement(query);
            statement.setInt(1, value);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDouble(String query, double value) {
        try {
            PreparedStatement statement = openConnection().prepareStatement(query);
            statement.setDouble(1, value);
            statement.executeUpdate();
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * End set value to DB
     *
     */

    /**
     *
     * Start getting value to DB
     *
     */


    @Override
    public String getString(String query) {
        try {
            PreparedStatement statement = openConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            return set.next() ? set.getString(1) : null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getInt(String query) {
        try {
            PreparedStatement statement = openConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            return set.next() ? set.getInt(1) : null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public double getDouble(String query) {
        try {
            PreparedStatement statement = openConnection().prepareStatement(query);
            ResultSet set = statement.executeQuery();
            return set.next() ? set.getDouble(1) : null;
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0D;
    }

    public Connection getConnection() {
        return connection;
    }

    private boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }

    @Override
    public Connection openConnection() throws ClassNotFoundException, SQLException {
        if (checkConnection()) {
            return getConnection();
        }

        String connectionURL = "jdbc:sqlite:" + db;

        Class.forName("org.sqlite.JDBC");

        connection = DriverManager.getConnection(connectionURL);

        return connection;
    }

    @Override
    public Config.IConfig init(Config config) throws SQLException, ClassNotFoundException {
            openConnection();

            try {
                Statement statement = openConnection().createStatement();
                statement.executeUpdate(Config.SQL_CREATE_QUAKE_TABLE);
            } catch (Exception e) {}
        return this;
    }
}
