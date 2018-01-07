package fr.bretzel.quake.config.online;

import fr.bretzel.quake.config.Config;

import java.sql.*;

public class OnlineConfig implements Config.IConfig {

    private Connection connection;

    private String hostname, username, password, database, port;

    /**
     * Creates a new MySQL instance for a specific database
     *
     * @param hostname Name of the host
     * @param port     Port number
     * @param database Database name
     * @param username Username
     * @param password Password
     */
    public OnlineConfig(String hostname, String username, String password, String port, String database) {
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.database = database;
        this.port = port;
    }


    private boolean checkConnection() throws SQLException {
        return connection != null && !connection.isClosed();
    }


    private Connection getConnection() {
        return connection;
    }

    @Override
    public Connection openConnection() throws ClassNotFoundException, SQLException {
        if (checkConnection()) {
            return getConnection();
        }

        String connectionURL = "jdbc:mysql://"
                + this.hostname + ":" + this.port;

        if (database != null || !database.isEmpty()) {
            connectionURL = connectionURL + "/" + this.database;
        }

        Class.forName("com.mysql.jdbc.Driver");

        connection = DriverManager.getConnection(connectionURL, this.username, this.password);

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

    /**
     *
     * End getting value to DB
     *
     */
}
